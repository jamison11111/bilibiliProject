package com.lwc.Dao;

import com.lwc.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: VideoDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/22 15:17
 */
@Mapper
public interface VideoDao {

    //发布一个视频时,把这个视频的相关信息也给插入到数据库里
    Integer addVideos(Video video);

    //给视频添加标签
    Integer batchAddVideoTags(List<VideoTag>videoTagList);

    //查询看看所查的分区有没有数据
    Integer pageCountVideos(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);

    Video getVideoById(Long videoId);
    /*以下这个方法形参数大于1,所以最好加@Param注解加以区分*/
    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId,@Param("userId") Long userId);

    void addVideoLike(@Param("videoId") Long videoId,@Param("userId")Long userId,@Param("createTime") Date createTime);

    Integer deleteVideoLike(VideoLike videoLike);

    Long getVideoLikes(Long videoId);


    CollectionGroup getCollectionGroupByGroupIdAndUserId(@Param("userId") Long userId,@Param("groupId") Long groupId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Integer deleteVideoCollectionById(@Param("videoId") Long videoId, @Param("userId") Long userId);

    Integer countVideoCollections(Long videoId);

    VideoCollection getVideoCollectionByuserIdAndVideoId(@Param("userId") Long userId,@Param("videoId") Long videoId);

    Integer addCollectionGroup(CollectionGroup collectionGroup);

    Integer deleteCollectionGroup(@Param("id") Long id, @Param("userId") Long userId);

    Integer updateCollectionGroup(CollectionGroup collectionGroup);

    List<CollectionGroup> getCollectionGroupListByUserId(Long userId);

    List<VideoCollection> getVideoCollectionByGroupId(Long groupId);

    Integer updateVideoCoins(VideoCoin videoCoin);

    VideoCoin getVideoCoinByUserIdAndVideoId(@Param("userId") Long userId,@Param("videoId") Long videoId);

    Integer postVideoCoins(VideoCoin videoCoin);

    Long getVideoCoinsAmount(Long videoId);

    Integer addVideoComment(VideoComment videoComment);

    Integer countVideoComments(Long videoId);

    List<VideoComment> pageListVideoComments(Map<String, Object> map);

    List<VideoComment> getcVideoCommentChildListByRootIdAndVideoId(@Param("rootId") Long rootId,@Param("videoId") Long videoId);

    List<VideoComment> getAllChildCommentByCommentIds(@Param("rootIdSet") Set<Long> parentCommentIdsSet);

    List<VideoComment> getChildCommentByRootId(Long id);

    Integer addVideoView(VideoView videoView);

    VideoView getVideoView(Map<String, Object> params);

    Integer getVideoViewCounts(Long videoId);

    List<UserPreference> getAllUserPreference();

    List<Video> batchGetVideosByIds(@Param("idList") List<Long> itemIds);

    Integer batchAddVideoBinaryPictures(List<VideoBinaryPicture> pictureList);
}
