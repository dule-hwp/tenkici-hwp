/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamengine.utils;

/**
 *
 * @author Dong
 */
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {

    private AudioInputStream soundStream;
    private URL soundFile;
    private Clip clip;
    private int type;//1 for sounds that needs to be played all the time
    // 2 for sounds that only need to be played once
//    private Runnable myRunnable;
    private boolean mMusicIsplaying;
    private String path;
    private boolean mClipIsReady;

    public SoundPlayer(int type, URL soundFile) {
        this.soundFile = soundFile;
        this.type = type;
        initClip();
        if (this.type == 1) {
            mMusicIsplaying = true;
            Runnable myRunnable = new Runnable() {
                public void run() {
                    while (mMusicIsplaying && mClipIsReady) {
                        clip.start();
                        clip.loop(clip.LOOP_CONTINUOUSLY);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
            Thread thread = new Thread(myRunnable);
            thread.start();
        }
    }

    public void initClip() {
        try {
//           getClass().getResource("../");
//            path = soundFile.toString();
            //read audio data from whatever source (file/classloader/etc.)
//            InputStream audioSrc = getClass().getResourceAsStream("mySound.au");
            //add buffer for mark/reset support
            InputStream bufferedIn = new BufferedInputStream(soundFile.openStream());
//            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            soundStream = AudioSystem.getAudioInputStream(bufferedIn);
//            soundStream = AudioSystem.getAudioInputStream(this.soundFile.openStream());
            clip = AudioSystem.getClip();
            clip.open(soundStream);
            mClipIsReady = true;
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\nNo sound documents are found"
                    + "\nPath: " + path);
            mClipIsReady = false;
        }
    }

    public SoundPlayer(int i) {
        this.type = i;
    }

    public void play() {
        if (mClipIsReady) {
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
        mMusicIsplaying = false;
    }

    public void setUrl(URL url) {
        soundFile = url;
        initClip();
    }
}
