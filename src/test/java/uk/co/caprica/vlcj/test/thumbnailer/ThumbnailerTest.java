package uk.co.caprica.vlcj.test.thumbnailer;

import uk.co.caprica.vlcj.enums.PictureType;
import uk.co.caprica.vlcj.enums.ThumbnailerSeekSpeed;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.Picture;
import uk.co.caprica.vlcj.media.ThumbnailRequest;
import uk.co.caprica.vlcj.player.events.media.MediaEventAdapter;
import uk.co.caprica.vlcj.test.VlcjTest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.CountDownLatch;

/**
 * Test the native thumbnailer.
 * <p>
 * Specify one or more MRL's as command-line arguments.
 */
public class ThumbnailerTest extends VlcjTest {

    private static MediaPlayerFactory factory;

    private static int x = 50;
    private static int y = 50;

    private static int width = 240;
    private static int height = 160;

    private static float position = 0.5f;

    public static void main(String[] args) {
        factory = new MediaPlayerFactory();

        String[] mrls = args;

        for (String mrl : mrls) {
            requestThumbnail(mrl);
        }

        factory.release();
    }

    private static void requestThumbnail(String mrl) {
        Media media = factory.media().newMedia(mrl);

        final CountDownLatch sync = new CountDownLatch(1);

        media.events().addMediaEventListener(new MediaEventAdapter() {
            @Override
            public void mediaThumbnailGenerated(Media media, Picture picture) {
                showImage(picture);
                sync.countDown();
            }
        });

        ThumbnailRequest request = media.thumbnails().requestByPosition(position, ThumbnailerSeekSpeed.FAST, width, height, PictureType.JPG, 30000);

        try {
            sync.await();
        }
        catch (InterruptedException e) {
        }

        media.release();
    }

    private static void showImage(Picture picture) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(picture.buffer()));

            JFrame f = new JFrame("Thumbnail");
            f.setLocation(x, y);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setContentPane(new ImagePane(ImagePane.Mode.DEFAULT, image, 1.0f));
            f.pack();
            f.setVisible(true);

            x += width + 10;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
