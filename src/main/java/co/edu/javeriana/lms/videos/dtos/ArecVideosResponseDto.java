package co.edu.javeriana.lms.videos.dtos;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArecVideosResponseDto {
    @SerializedName("page_info")
    private PageInfo pageInfo;
    private List<Video> result;

    @Data
    public static class PageInfo {
        private Integer total;
        private Integer count;
    }

    @Data
    public static class Video {
        private String name;
        private Double length;
        @SerializedName("recorded_at")
        private Date recordedAt;
        @SerializedName("finished_at")
        private Date finishedAt;
        private String status;
        @SerializedName("videos")
        private List<VideoMetadata> metadata;
    }

    @Data
    public static class VideoMetadata {
        @SerializedName("channel_name")
        private String channelName;
        @SerializedName("playback_url")
        private String playbackUrl;
        @SerializedName("download_url")
        private String downloadUrl;
        private Double size;
        private String thumbnail;
    }
}
