package com.eggplant.qiezisocial.rtc;

import android.opengl.EGLContext;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Administrator on 2018/12/10.
 */

public class WebRtcClient {
    private static final String TAG = "WebRtcClient";

    private final static int MAX_PEER=2;
    private boolean[] endPoints=new boolean[MAX_PEER];
    private PeerConnectionFactory factory;
    private HashMap<String, Peer> peers=new HashMap<>();
    private LinkedList<PeerConnection.IceServer> iceServers=new LinkedList<>();
    private PeerConnectionParameters pcParams;
    private MediaConstraints pcConstraints=new MediaConstraints();
    private MediaStream localMS;
    private VideoSource videoSource;
    private RtcListener mListener;

    private String mClientId;
    private String mRoomId;
    private HashMap<String, Command> commandMap;
    private AudioSource audioSource;
    private AudioTrack audioTrack;
    private VideoTrack videoTrack;
    private VideoCapturerAndroid videoCapture;


    public WebRtcClient(RtcListener listener, PeerConnectionParameters params, EGLContext mEGLcontext){
        mListener = listener;
        pcParams = params;
        this.commandMap = new HashMap<>();
        commandMap.put("connect", new CreateOfferCommand());
        commandMap.put("offer", new CreateAnswerCommand());
        commandMap.put("answer", new SetRemoteSDPCommand());
        commandMap.put("candidate", new AddIceCandidateCommand());

        PeerConnectionFactory.initializeAndroidGlobals(listener, true, true,
                params.videoCodecHwAcceleration, mEGLcontext);
        factory = new PeerConnectionFactory();

        iceServers.add(new PeerConnection.IceServer("turn:120.27.197.92:3478 ","qiezigoutong","qzgtyes"));
        iceServers.add(new PeerConnection.IceServer("stun:120.27.197.92:3478"));

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
    }

    /**
     * Implement this interface to be notified of events.
     */
    public interface RtcListener {

        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream, int endPoint);
        void onAudioStream();

        void onRemoveRemoteStream(int endPoint);

        void sendSDPMsg(String id, SessionDescription sdp, JSONObject payload);
        void sendCandidateMsg(String id, String candiadate, JSONObject payload);
    }

    private interface Command {
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    private class CreateOfferCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "CreateOfferCommand");
            Peer peer = peers.get(peerId);
            peer.pc.createOffer(peer, pcConstraints);
        }
    }

    private class CreateAnswerCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "CreateAnswerCommand"+payload);
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );

            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }
    }

    private class SetRemoteSDPCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "SetRemoteSDPCommand");
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    private class AddIceCandidateCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d(TAG, "AddIceCandidateCommand");
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("id"),
                        payload.getInt("label"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }



    private class Peer implements SdpObserver, PeerConnection.Observer {
        private PeerConnection pc;
        private String id;
        private int endPoint;
        private VideoTrack remoteTrack;

        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            // TODO: modify sdp to use pcParams prefered codecs
            try {
                JSONObject payload = new JSONObject();
                payload.put("type", sdp.type.canonicalForm());
                payload.put("sdp", sdp.description);
//                sendMessage(mRoomId, mClientId, id, sdp.type.canonicalForm(), payload);
                mListener.sendSDPMsg(id,sdp,payload);
                pc.setLocalDescription(Peer.this, sdp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSetSuccess() {
            Log.e(TAG, "onSetSuccess: " );
        }

        @Override
        public void onCreateFailure(String s) {
            Log.e(TAG, "onCreateFailure: "+s );
        }

        @Override
        public void onSetFailure(String s) {
            Log.e(TAG, "onSetFailure: " +s);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.e(TAG, "onSignalingChange: ");
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                removePeer(id);
                mListener.onStatusChanged("DISCONNECTED");
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            try {
                JSONObject payload = new JSONObject();
                payload.put("label", candidate.sdpMLineIndex);
                payload.put("id", candidate.sdpMid);
                payload.put("candidate", candidate.sdp);
                payload.put("socketId","1008611");
//                sendMessage(mRoomId, mClientId, id, "candidate", payload);
                mListener.sendCandidateMsg(id,"candidate",payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream remoteStream) {
//            Log.d(TAG, "onAddStream " + mediaStream.label());
            // remote streams are displayed from 1 to MAX_PEER (0 is localStream)
            if(pcParams.videoCallEnabled) {
                remoteTrack = remoteStream.videoTracks.get(0);
                mListener.onAddRemoteStream(remoteStream, endPoint + 1);
            }else {
                mListener.onAudioStream();
            }
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "onRemoveStream " + mediaStream.label());
            removePeer(id);
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        public Peer(String id, int endPoint) {
            Log.d(TAG, "new Peer: " + id + " " + endPoint);
            this.pc = factory.createPeerConnection(iceServers, pcConstraints, this);
            this.id = id;
            this.endPoint = endPoint;
            pc.addStream(localMS); //, new MediaConstraints()
            mListener.onStatusChanged("CONNECTING");
        }
    }

    public void connect(String toid) throws JSONException {
        if (!peers.containsKey(toid)) {
            // if MAX_PEER is reach, ignore the call
            int endPoint = findEndPoint();
            if (endPoint != MAX_PEER) {
                Peer peer = addPeer(toid, endPoint);
                peer.pc.addStream(localMS);
                commandMap.get("connect").execute(toid, null);
            }
        } else {
            commandMap.get("connect").execute(toid, null);
        }
    }


    public void start() {
        setCamera();
    }

    private void setCamera() {
        localMS = factory.createLocalMediaStream("ARDAMS");
        if (pcParams.videoCallEnabled) {
            MediaConstraints videoConstraints = new MediaConstraints();
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight", Integer.toString(pcParams.videoHeight)));
//            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", Integer.toString(pcParams.videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minWidth", "640"));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth", "640"));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight","480"));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minHeight", "480"));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(pcParams.videoFps)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(pcParams.videoFps)));
            videoCapture =  getVideoCapturer();
            videoSource = factory.createVideoSource(videoCapture, videoConstraints);
            videoTrack = factory.createVideoTrack("ARDAMSv0", videoSource);
            localMS.addTrack(videoTrack);
        }

        audioSource = factory.createAudioSource(new MediaConstraints());
        audioTrack = factory.createAudioTrack("ARDAMSa0", audioSource);
        localMS.addTrack(audioTrack);
        mListener.onLocalStream(localMS);

    }
    public void startAudio(){
        localMS = factory.createLocalMediaStream("ARDAMS");
        audioSource = factory.createAudioSource(new MediaConstraints());
        audioTrack = factory.createAudioTrack("ARDAMSa0", audioSource);
        localMS.addTrack(audioTrack);
    }

    private VideoCapturerAndroid getVideoCapturer() {
        String frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        return VideoCapturerAndroid.create(frontCameraDeviceName, new VideoCapturerAndroid.CameraErrorHandler() {
            @Override
            public void onCameraError(String s) {

            }
        });
    }

    public void switchCamera(){
        videoCapture.switchCamera(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
    public void  mute(){
//        if ()
//        audioTrack.state()
//        audioTrack.setState(MediaStreamTrack.State.ENDED);


    }
    public void unMute(){

    }







    private Peer addPeer(String id, int endPoint) {
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);

        endPoints[endPoint] = true;
        return peer;
    }

    private void removePeer(String id) {
        Peer peer = peers.get(id);
        mListener.onRemoveRemoteStream(peer.endPoint);
        peer.pc.close();
        peers.remove(peer.id);
        endPoints[peer.endPoint] = false;
    }



    /**
     * Call this method in Activity.onPause()
     */
    public void onPause() {
        if (videoSource != null) videoSource.stop();
    }

    /**
     * Call this method in Activity.onResume()
     */
    public void onResume() {
        if (videoSource != null) videoSource.restart();
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        if (videoTrack!=null) {
            localMS.removeTrack(videoTrack);
//            videoTrack.dispose();
            videoTrack=null;
        }
        if (audioTrack!=null){
            localMS.removeTrack(audioTrack);
//            audioTrack.dispose();
            audioTrack=null;
        }
        if (videoSource != null) {
//            videoSource.dispose();
            videoSource=null;
        }
        if (audioSource!=null){
//            audioSource.dispose();
            audioSource=null;
        }
        for (Peer peer : peers.values()) {
            peer.pc.close();
            peer.pc.dispose();
        }


        factory.dispose();
    }

    private int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    public void onEvent(String from,String type, JSONObject payload) throws JSONException {
        if (!peers.containsKey(from)) {
            // if MAX_PEER is reach, ignore the call
            int endPoint = findEndPoint();
            if (endPoint != MAX_PEER) {
                Peer peer = addPeer(from, endPoint);
                peer.pc.addStream(localMS);
                commandMap.get(type).execute(from, payload);
            }
        } else {
            commandMap.get(type).execute(from, payload);
        }
    }
}
