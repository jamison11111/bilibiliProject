package com.lwc.service;

import com.lwc.Dao.VideoDao;
import com.lwc.domain.*;
import com.lwc.domain.exception.ConditionException;
import com.lwc.service.util.FastDFSUtil;
import com.lwc.service.util.ImageUtil;
import com.lwc.service.util.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItem;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.bytedeco.javacv.Frame;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: VideoService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/22 15:17
 */
@Service
public class VideoService {
    @Autowired
    private VideoDao videoDao;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private UserCoinService userCoinService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired//调用百度人像分割相关的api的工具类
    private ImageUtil imageUtil;

    private static final int FRAME_NO=16;//每隔16帧做一次视频图片的截取
    //用户添加视频时,也会传视频标签,将其作为冗余字段参与VideoTag数据的制作
    @Transactional//多次数据库操作必须同时成功
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideos(video);
        Long id = video.getId();
        //补充视频类的冗余字段，将VideoTag关联表的信息插入数据库
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> {
            item.setVideoId(id);
            item.setCreateTime(now);
        });
        videoDao.batchAddVideoTags(tagList);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == 0) {
            throw new ConditionException("参数异常!");
        }
        //需要知道从第几条数据开始查，要查多少条，知道这些后就能进数据库了
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        List<Video> list = new ArrayList<>();
        //先做第一次筛选,去数据库查询看看符合条件的数据有多少条，就是说看看所查的分区有没有数据
        Integer total = videoDao.pageCountVideos(params);
        if (total > 0) {
            //真正开始查页面数据
            list = videoDao.pageListVideos(params);
        }
        PageResult<Video> result = new PageResult<>(total, list);
        return result;


    }

    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        //此api负责把请求到的资源灌入到输出流中，然后再把输出流写道响应体中返回给前端
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);

    }

    public void addVideoLike(Long videoId, Long userId) {
        //首先，判断下这个视频存不存在
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频不存在");
        }
        //检查这个点赞操作是否已经做过
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if (videoLike != null) {
            throw new ConditionException("你已点赞过改视频");
        }
        Date createTime = new Date();
        videoDao.addVideoLike(videoId, userId, createTime);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        //首先，判断下这个视频存不存在
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频不存在");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if (videoLike == null) {
            throw new ConditionException("你未点赞过改视频");
        }
        videoDao.deleteVideoLike(videoLike);
    }

    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        Long count = videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        Map<String, Object> result = new HashMap<>();
        if (videoLike == null) {
            result.put("like", false);
        } else result.put("like", true);
        result.put("count", count);
        return result;
    }

    //只有一个新增操作，不用加事务注解也可以
    public void addVideoCollection(VideoCollection videoCollection) {
        Long videoId = videoCollection.getVideoId();
        Long userId = videoCollection.getUserId();
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频不存在");
        }

        //查看分组是否存在
        CollectionGroup collectionGroup = videoDao.getCollectionGroupByGroupIdAndUserId(userId, videoCollection.getGroupId());
        if (collectionGroup == null) {
            throw new ConditionException("所选分组不存在");
        }
        videoCollection.setCreateTime(new Date());
        //之前可能收藏过这个视频，所以需要先删除后新增
        videoDao.deleteVideoCollectionById(videoId, userId);
        videoDao.addVideoCollection(videoCollection);
    }

    public void deleteVideoCollection(Long videoId, Long userId) {
        //视频不存在或者视频根本就没收藏过，则删了个寂寞罢了，无需判断
        videoDao.deleteVideoCollectionById(videoId, userId);
    }

    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        Integer count = videoDao.countVideoCollections(videoId);
        result.put("count", count);
        VideoCollection videoCollection = videoDao.getVideoCollectionByuserIdAndVideoId(userId, videoId);
        boolean collect = (videoCollection != null);
        result.put("collect", collect);
        return result;
    }

    public void addCollectionGroup(CollectionGroup collectionGroup) {
        collectionGroup.setCreateTime(new Date());
        videoDao.addCollectionGroup(collectionGroup);
    }

    public void deleteCollectinoGroup(Long id, Long userId) {
        videoDao.deleteCollectionGroup(id, userId);
    }


    public void updateCollectionGroup(CollectionGroup collectionGroup) {
        collectionGroup.setUpdateTime(new Date());
        videoDao.updateCollectionGroup(collectionGroup);

    }

    public List<CollectionGroup> getCollectionGroups(Long userId) {
        List<CollectionGroup> groupList = videoDao.getCollectionGroupListByUserId(userId);
        //先不管花里胡哨的java新特性,能实现业务逻辑就行
        for (CollectionGroup collectionGroup : groupList) {
            //存放每个分组的视频集合
            List<VideoCollection> videoCollectionList = new ArrayList<>();
            Long id = collectionGroup.getId();
            videoCollectionList = videoDao.getVideoCollectionByGroupId(id);
            collectionGroup.setVideoCollections(videoCollectionList);
        }
        return groupList;
    }

    @Transactional//涉及两张表的修改,加事务注解
    public void addVideoCoins(VideoCoin videoCoin) {
        //查询要投币的视频是否存在...
        Long userId = videoCoin.getUserId();
        Long videoId = videoCoin.getVideoId();
        if (videoId == null || videoId == 0) {
            throw new ConditionException("非法传参");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频不存在");
        }
        //判断用户的硬币数量是否足够
        Integer postAmount = videoCoin.getAmount();//想要投的硬币数量
        Integer coinsAmount = userCoinService.getUserCoinsAmount(userId);
        coinsAmount = coinsAmount == null ? 0 : coinsAmount;
        if (postAmount > coinsAmount) {
            throw new ConditionException("用户硬币数量不足,无法投币");
        }
        //查询之前是否给这个视频投过币
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByUserIdAndVideoId(userId, videoId);
        if (dbVideoCoin != null) {
            videoCoin.setUpdateTime(new Date());
            videoCoin.setAmount(dbVideoCoin.getAmount() + postAmount);
            videoDao.updateVideoCoins(videoCoin);
        } else {
            videoCoin.setCreateTime(new Date());
            videoDao.postVideoCoins(videoCoin);
        }
        //更新用户硬币数量
        userCoinService.updateUserCoin(userId, (long) (coinsAmount - postAmount));
    }

    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        Map<String, Object> map = new HashMap<>();
        Long count = videoDao.getVideoCoinsAmount(videoId);
        map.put("count", count);
        VideoCoin videoCollection = videoDao.getVideoCoinByUserIdAndVideoId(userId, videoId);
        Boolean postStatus = (videoCollection != null);
        map.put("postStatus", postStatus);
        return map;
    }

    public void addVideoComment(VideoComment videoComment, Long userId) {
        /*判断视频是否存在*/
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数非法");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频不存在");
        }
        videoComment.setUserId(userId);//设置当前评论的用户的id
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }

    public PageResult<VideoComment> pageListVideoComments(Integer no, Integer size, Long videoId) {
        /*判断视频是否存在*/
        if (videoId == null) {
            throw new ConditionException("参数非法");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频不存在");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);
        //查询一级评论的条数
        Integer total = videoDao.countVideoComments(videoId);
        List<VideoComment> list = new ArrayList<>();
        if (total > 0) {
            list = videoDao.pageListVideoComments(params);
            //将一级评论和二级评论的用户信息都查出来,查询sql数据库的次数越少越好
            Set<Long> parentUserIdsSet = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            List<UserInfo> userInfos = userService.getUserInfoByUserIds(parentUserIdsSet);
            Set<Long> parentCommentIdsSet = list.stream().map(VideoComment::getId).collect(Collectors.toSet());
            List<VideoComment> childCommentList = videoDao.getAllChildCommentByCommentIds(parentCommentIdsSet);
            Set<Long> replyUserIdsSet = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            List<UserInfo> replyUserInfos = userService.getUserInfoByUserIds(replyUserIdsSet);
            //冗余字段大封装，从外到里
            list.forEach(parentComment -> {
                Long userId = parentComment.getUserId();
                Long id = parentComment.getId();
                List<VideoComment> childComments = new ArrayList<>();
                userInfos.forEach(userInfo -> {
                    if (userId.equals(userInfo.getUserId())) {
                        parentComment.setUserInfo(userInfo);
                    }
                });
                childCommentList.forEach(childComment -> {
                    Long childUserId = childComment.getUserId();
                    Long replyUserId = childComment.getReplyUserId();
                    replyUserInfos.forEach(replyUserInfo -> {
                        if (replyUserId.equals(replyUserInfo.getUserId())) {
                            childComment.setReplyUserInfo(replyUserInfo);
                        }
                    });
                    userInfos.forEach(userInfo -> {
                        if (childUserId.equals(userInfo.getUserId())) {
                            childComment.setUserInfo(userInfo);
                        }
                    });
                    Long rootId = childComment.getRootId();
                    if (rootId.equals(id)) {
                        childComments.add(childComment);
                    }
                });
                parentComment.setChildList(childComments);
            });
        }
        return new PageResult<VideoComment>(total, list);

    }

    public Map<String, Object> getVideoDetails(Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        Long userId = video.getUserId();
        User user = userService.getUserById(userId);
        UserInfo userInfo = user.getUserInfo();
        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("userInfo", userInfo);
        return result;
    }

    public void addVideoView(VideoView videoView, HttpServletRequest request) {
        Long userId = videoView.getUserId();
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        String clientId = String.valueOf(userAgent.getId());
        String ip = IpUtil.getIP(request);

        //创建一个Map,存放所有去数据库查询观看记录时所需要的信息
        Map<String, Object> params = new HashMap<>();
        params.put("videoId", videoView.getVideoId());
        params.put("userId", userId);
        params.put("clientId", clientId);
        params.put("ip",ip);
        Date now=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        params.put("today",sdf.format(now));
        //去数据库查询今天该用户/游客有没有产生过观看记录
        VideoView dbVideoView=videoDao.getVideoView(params);
        if(dbVideoView==null){
            videoView.setClientId(clientId);
            videoView.setIp(ip);
            videoView.setCreateTime(now);
            videoDao.addVideoView(videoView);
        }
        //查得到dbVideoView则什么也不做
    }

    public Integer getVideoViewCounts(Long videoId) {
        return videoDao.getVideoViewCounts(videoId);
    }

    public List<Video> recommend(Long userId) throws TasteException {
        /*
        从用户操作表中计算出所有用户的用户-视频分数表并以列表形式返回,
        需要从上帝视角查询所有用户喜好,而不只是当前用户的喜好
         */
        List<UserPreference>list=videoDao.getAllUserPreference();
        //创建数据模型,模型中包含了Map<userId,List<UserPreference>>的信息,但这个Map是Mahout引擎的特化Map
        DataModel dataModel=this.createDataModel(list);
        //获取用户相似度
        UserSimilarity similarity=new UncenteredCosineSimilarity(dataModel);
        //打印1,2号用户的相似度(userId:1,2,测试行)
        System.out.println(similarity.userSimilarity(1,2));
        //获取用户邻居(多态形式创建接口的实现类对象)
        UserNeighborhood userNeighborhood=new NearestNUserNeighborhood(2,similarity,dataModel);
        //获取特定用户的2个邻居的id
        long[] ar=userNeighborhood.getUserNeighborhood(userId);
        //构建推荐器
        Recommender recommender=new GenericUserBasedRecommender(dataModel,userNeighborhood,similarity);
        //推荐视频
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 5);
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return videoDao.batchGetVideosByIds(itemIds);
    }

    /**
     * 基于内容的协同推荐
     * @param userId 用户id
     * @param itemId 参考内容id（根据该内容进行相似内容推荐）
     * @param howMany 需要推荐的数量
     */
    public List<Video> recommendByItem(Long userId, Long itemId, int howMany) throws TasteException {
        List<UserPreference> list = videoDao.getAllUserPreference();
        //创建数据模型
        DataModel dataModel = this.createDataModel(list);
        //获取内容相似程度
        ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(dataModel, similarity);
        // 物品推荐相拟度，计算两个物品同时出现的次数，次数越多任务的相拟度越高
        List<Long> itemIds = genericItemBasedRecommender.recommendedBecause(userId, itemId, howMany)
                .stream()
                .map(RecommendedItem::getItemID)
                .collect(Collectors.toList());
        //推荐视频
        return videoDao.batchGetVideosByIds(itemIds);
    }
    //获取Mahout推荐推荐引擎数据模型的内部私有接口
    private DataModel createDataModel(List<UserPreference> userPreferenceList) {
        FastByIDMap<PreferenceArray>fastByIdMap=new FastByIDMap<>();
        //将列表流按userId分组,自然就生成Map
        Map<Long, List<UserPreference>> map = userPreferenceList.stream().collect(Collectors.groupingBy(UserPreference::getUserId));
        //列表集合
        Collection<List<UserPreference>> list = map.values();
        for(List<UserPreference>userPreferences:list){
            //用户偏好需要封装到这个新类型数组中
            GenericPreference[] array=new GenericPreference[userPreferences.size()];
            for(int i=0;i<userPreferences.size();i++){
                UserPreference userPreference=userPreferences.get(i);
                //封装是只用封装这三个字段，因为只有实际上这三个字段有用
                GenericPreference item=new GenericPreference(userPreference.getUserId(),userPreference.getVideoId(),userPreference.getValue());
                array[i]=item;
            }
            //转换为搜索引擎能够进性分析使用的Map类
            fastByIdMap.put(array[0].getUserID(),new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }

    public List<VideoBinaryPicture> convertVideoToImage(Long videoId, String fileMd5) throws Exception{
        File file = fileService.getFileByMd5(fileMd5);
        //先把要进行弹幕遮罩的视频文件从DFS下载到本地的临时文件中
        String filePath = "/Users/hat/tmpfile/fileForVideoId" + videoId + "." + file.getType();
        fastDFSUtil.downLoadFile(file.getUrl(), filePath);
        //创建视频截取帧类对象,并开始截取视频帧
        FFmpegFrameGrabber fFmpegFrameGrabber = FFmpegFrameGrabber.createDefault(filePath);
        fFmpegFrameGrabber.start();
        int ffLength = fFmpegFrameGrabber.getLengthInFrames();
        Frame frame;
        Java2DFrameConverter converter = new Java2DFrameConverter();
        int count = 1;//计数器,用于每隔一个频率的帧进行一次帧截取这个功能的实现
        List<VideoBinaryPicture> pictures = new ArrayList<>();
        for(int i=1; i<= ffLength; i ++){
            long timestamp = fFmpegFrameGrabber.getTimestamp();
            frame = fFmpegFrameGrabber.grabImage();
            if(count == i){//当前遍历的帧i=count时,才需要对截取下来的帧做处理,否则不做任何操作(进不了if循环)
                if(frame == null){
                    throw new ConditionException("无效帧");
                }
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", os);
                InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                //输出黑白剪影文件
                //创建一个临时文件,用于存储百度api生成的黑白剪影图
                java.io.File outputFile = java.io.File.createTempFile("convert-" + videoId + "-", ".png");
                //调用百度api生成黑白剪影图
                BufferedImage binaryImg = imageUtil.getBodyOutline(bufferedImage, inputStream);
                //将剪影图写进临时文件
                ImageIO.write(binaryImg, "png", outputFile);
                //有的浏览器或网站需要把图片白色的部分转为透明色，使用以下方法可实现
                imageUtil.transferAlpha(outputFile, outputFile);
                //上传视频剪影图片帧文件
                String imgUrl = fastDFSUtil.uploadCommonFile((MultipartFile) outputFile);
                VideoBinaryPicture videoBinaryPicture = new VideoBinaryPicture();
                videoBinaryPicture.setFrameNo(i);
                videoBinaryPicture.setUrl(imgUrl);
                videoBinaryPicture.setVideoId(videoId);
                videoBinaryPicture.setVideoTimeStamp(timestamp);
                pictures.add(videoBinaryPicture);
                count += FRAME_NO;
                //删除临时文件
                outputFile.delete();
            }
        }
        //删除一开始从fastDFS文件服务器下载下来的视频文件(临时文件)
        java.io.File tmpFile = new java.io.File(filePath);
        tmpFile.delete();
        //批量添加视频剪影文件,将剪影图的url地址等信息写入数据库
        videoDao.batchAddVideoBinaryPictures(pictures);
        return pictures;
    }
}
