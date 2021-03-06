package uk.co.caprica.vlcj.test.factory;

import com.sun.jna.Pointer;
import uk.co.caprica.vlcj.factory.*;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;
import uk.co.caprica.vlcj.test.VlcjTest;

import javax.swing.*;
import java.awt.*;

// Interesting...
//  the old problem of playMedia returning true but not found - well, now we will get an error callback
// Still does nothing for subtitle files

public class DialogsTest extends VlcjTest {

    private static final String username = "not a real username";

    private static final String password = "not a real password";

    // This stream is protected by a username/password
    private static final String mrl = "http://127.0.0.1:5555";

    private static int loginCount = 0;

    public static void main(String[] args) throws Exception {
        // Must specify a keystore or it will appear to hang or just not work
        final MediaPlayerFactory factory = new MediaPlayerFactory("--keystore=secret");

        Canvas canvas = new Canvas();
        canvas.setBackground(Color.black);

        VideoSurface videoSurface = factory.videoSurfaces().newVideoSurface(canvas);

        JFrame f = new JFrame();
        f.setBounds(100, 100, 800, 600);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(canvas, BorderLayout.CENTER);
        f.setVisible(true);

        Dialogs dialogs = factory.dialogs().newDialogs();

        factory.dialogs().enable(dialogs, null);

        dialogs.addDialogHandler(new DialogHandler() {
            @Override
            public void displayError(Pointer userData, String title, String text) {
                System.out.printf("display error: %d - %s - %s%n", userData, title, text);
            }

            @Override
            public void displayLogin(Pointer userData, DialogId id, String title, String text, String defaultUsername, boolean askStore) {
                System.out.printf("display login: %s - %s - %s - %s - %s - %s%n", userData, id, title, text, defaultUsername, askStore);
                // Obviously we'd prompt for username and password instead of this...
                if (loginCount < 3) {
                    loginCount++;
                    factory.dialogs().postLogin(id, username, password, false);
                } else {
                    factory.dialogs().dismiss(id);
                }
            }

            @Override
            public void displayQuestion(Pointer userData, DialogId id, String title, String text, DialogQuestionType type, String cancel, String action1, String action2) {
            }

            @Override
            public void displayProgress(Pointer userData, DialogId id, String title, String text, int indeterminate, float position, String cancel) {
            }

            @Override
            public void cancel(Pointer userData, DialogId id) {
            }

            @Override
            public void updateProgress(Pointer userData, DialogId id, float position, String text) {
            }
        });

//        NativeLog log = factory.application().newLog();
//        log.setLevel(LogLevel.DEBUG);
//
//        log.addLogListener(new LogEventListener() {
//            @Override
//            public void log(LogLevel level, String module, String file, Integer line, String name, String header, Integer id, String message) {
//                System.out.printf("log: %s - %s - %s - %s%n", module, name, header, message);
//            }
//        });

        MediaPlayer mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
        ((EmbeddedMediaPlayer) mediaPlayer).videoSurface().setVideoSurface(videoSurface);

        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            }
        });

        boolean started = mediaPlayer.media().playMedia(mrl);
        System.out.println("started=" + started);

        Thread.currentThread().join();
    }

}
