package uk.co.caprica.vlcj.media;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_thumbnail_request_t;
import uk.co.caprica.vlcj.enums.PictureType;
import uk.co.caprica.vlcj.enums.ThumbnailerSeekSpeed;

public class ThumbnailService extends BaseService {

    ThumbnailService(Media media) {
        super(media);
    }

    public ThumbnailRequest requestByTime(long time, ThumbnailerSeekSpeed speed, int width, int height, PictureType pictureType, long timeout) {
        libvlc_media_thumbnail_request_t request = libvlc.libvlc_media_thumbnail_request_by_time(mediaInstance, time, speed.intValue(), width, height, pictureType.intValue(), timeout);
        if (request != null) {
            return new ThumbnailRequest(request);
        } else {
            return null;
        }
    }

    public ThumbnailRequest requestByPosition(float position, ThumbnailerSeekSpeed speed, int width, int height, PictureType pictureType, long timeout) {
        libvlc_media_thumbnail_request_t request = libvlc.libvlc_media_thumbnail_request_by_pos(mediaInstance, position, speed.intValue(), width, height, pictureType.intValue(), timeout);
        if (request != null) {
            return new ThumbnailRequest(request);
        } else {
            return null;
        }
    }

    public void cancel(ThumbnailRequest request) {
        libvlc.libvlc_media_thumbnail_cancel(request.instance());
    }

}
