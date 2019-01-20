package uk.co.caprica.vlcj.media;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_thumbnail_request_t;

public final class ThumbnailRequest {

    private final libvlc_media_thumbnail_request_t request;

    ThumbnailRequest(libvlc_media_thumbnail_request_t request) {
        this.request = request;
    }

    libvlc_media_thumbnail_request_t instance() {
        return this.request;
    }

}
