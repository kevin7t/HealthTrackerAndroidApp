package kevin.androidhealthtracker;

import com.kevin.healthtracker.datamodels.Friend;
import com.kevin.healthtracker.datamodels.User;
import com.kevin.healthtracker.datamodels.dto.LikeDTO;
import com.kevin.healthtracker.datamodels.dto.ReplyDTO;
import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class WebClient {
    private String url;
    private RestTemplate restTemplate;

    public WebClient(RestTemplate restTemplate, String host, int port ){
        this.url = String.format("http://%s:%d/healthtracker", host, port);
        this.restTemplate = restTemplate;
    }
    public User registerUser(User user) {
        return restTemplate.postForObject(url + "/users/register", user, User.class);
    }

    public Boolean loginUser(User user) {
        return restTemplate.postForObject(url + "/users/login", user, Boolean.class);
    }

    public User changePassword(User user) {
        return restTemplate.postForObject(url + "/users/changepassword", user, User.class);
    }

    public User getUser(int id) {
        return restTemplate.getForObject(url + "/users/" + id, User.class);
    }

    public User[] getAllUsers() {
        return restTemplate.getForObject(url + "/users", User[].class);
    }

    public void deleteUser(int id) {
        restTemplate.delete(url + "/users/" + id);
    }

    //Todo null object as request?
    public Friend addFriend(int user1Id, int user2Id) {
        return restTemplate.postForObject(String.format("%s/users/addfriend/%s/%s", url, user1Id, user2Id), null, Friend.class);
    }

    public Friend acceptFriend(int user1Id, int user2Id) {
        return restTemplate.postForObject(String.format("%s/users/acceptfriend/%s/%s", url, user1Id, user2Id), null, Friend.class);
    }

    public Friend declineFriend(int user1Id, int user2Id) {
        return restTemplate.postForObject(String.format("%s/users/declinefriend/%s/%s", url, user1Id, user2Id), null, Friend.class);
    }

    public void deleteFriend(int user1Id, int user2Id) {
        restTemplate.delete(String.format("%s/users/deletefriend/%s/%s", url, user1Id, user2Id), null, Friend.class);
    }

    public Friend[] getOutboundRequests(int user1Id) {
        return restTemplate.getForObject(url + "/users/getoutboundrequests/" + user1Id, Friend[].class);
    }

    public Friend[] getInboundRequests(int user1Id) {
        return restTemplate.getForObject(url + "/users/getinboundrequests/" + user1Id, Friend[].class);
    }

    public Friend[] getAllFriends(int user1Id) {
        return restTemplate.getForObject(url + "/users/getallrelations/" + user1Id, Friend[].class);
    }

    public Friend getFriend(int user1Id, int user2Id) {
        return restTemplate.getForObject(String.format("%s/users/getfriend/%s/%s", url, user1Id, user2Id), Friend.class);
    }

    /**
     * User feed functions
     */

    public StatusDTO addStatus(StatusDTO statusDTO) {
        return restTemplate.postForObject(url + "/feed/status", statusDTO, StatusDTO.class);
    }

    public StatusDTO[] getStatus(int userId, int pageNumber) {
        return restTemplate.getForObject(UriComponentsBuilder.fromHttpUrl(url + "/feed/status/user/" + userId).queryParam("page", pageNumber).build().toUri(), StatusDTO[].class);
    }

    public void deleteStatus(int statusId) {
        restTemplate.delete(url + "/feed/status/" + statusId);
    }

    public LikeDTO addLike(LikeDTO likeDTO) {
        return restTemplate.postForObject(url + "/feed/status/like", likeDTO, LikeDTO.class);
    }

    public LikeDTO[] getLikes(int statusId) {
        return restTemplate.getForObject(url + "/feed/status/like/" + statusId, LikeDTO[].class);
    }

    public void removeLike(int statusId, int userId) {
        restTemplate.delete(url + "/feed/status/like/" + statusId + userId);
    }

    public ReplyDTO sendReply(ReplyDTO replyDTO) {
        return restTemplate.postForObject(url + "/feed/status/reply", replyDTO, ReplyDTO.class);
    }

    public ReplyDTO[] getReplies(int statusId) {
        return restTemplate.getForObject(url + "/feed/status/reply/" + statusId, ReplyDTO[].class);
    }

    public void deleteReply(int replyId) {
        restTemplate.delete(url + "/feed/status/reply/" + replyId);
    }

}
