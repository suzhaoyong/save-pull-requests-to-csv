package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String uri = MessageFormat.format("https://api.github.com/repos/{0}/pulls", repo);

        // 获取指定 repo 的 JSON 数据
        String pullRequestsJSONData = getJsonData(uri);
        // 将 JSON 数据解析为 List
        List<PullRequestBean> pullRequestBeanList = JSON.parseArray(pullRequestsJSONData, PullRequestBean.class);
        // 从 List 获取指定数目的需要的信息
        List<String> limitedPullRequestInfoList = getLimitedPullRequestInfoList(pullRequestBeanList, n);
        // 将数据写入文件
        writeDataToCSV(limitedPullRequestInfoList, csvFile);
    }

    private static void writeDataToCSV(List<String> dataList, File csvFile) throws IOException {
        dataList.add(0, "number,author,title");
        FileUtils.writeLines(csvFile, dataList);
    }

    private static String getJsonData(String uri) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
    }

    private static String getPullRequestInfo(PullRequestBean pullRequestBean) {
        return pullRequestBean.getNumber() + "," + pullRequestBean.getUser().getLogin() + "," + pullRequestBean.getTitle();
    }

    private static List<String> getLimitedPullRequestInfoList(List<PullRequestBean> pullRequestBeanList, int n) {
        List<String> limitedPullRequestInfoList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            limitedPullRequestInfoList.add(getPullRequestInfo(pullRequestBeanList.get(i)));
        }
        return limitedPullRequestInfoList;
    }

    private static class PullRequestBean {
        /**
         * url : https://api.github.com/repos/golang/go/pulls/34832
         * id : 326899106
         * node_id : MDExOlB1bGxSZXF1ZXN0MzI2ODk5MTA2
         * html_url : https://github.com/golang/go/pull/34832
         * diff_url : https://github.com/golang/go/pull/34832.diff
         * patch_url : https://github.com/golang/go/pull/34832.patch
         * issue_url : https://api.github.com/repos/golang/go/issues/34832
         * number : 34832
         * state : open
         * locked : false
         * title : expvar: make possible to delete all exported variables
         * user : {"login":"artemyarulin","id":6191712,"node_id":"MDQ6VXNlcjYxOTE3MTI=","avatar_url":"https://avatars0.githubusercontent.com/u/6191712?v=4","gravatar_id":"","url":"https://api.github.com/users/artemyarulin","html_url":"https://github.com/artemyarulin","followers_url":"https://api.github.com/users/artemyarulin/followers","following_url":"https://api.github.com/users/artemyarulin/following{/other_user}","gists_url":"https://api.github.com/users/artemyarulin/gists{/gist_id}","starred_url":"https://api.github.com/users/artemyarulin/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/artemyarulin/subscriptions","organizations_url":"https://api.github.com/users/artemyarulin/orgs","repos_url":"https://api.github.com/users/artemyarulin/repos","events_url":"https://api.github.com/users/artemyarulin/events{/privacy}","received_events_url":"https://api.github.com/users/artemyarulin/received_events","type":"User","site_admin":false}
         * body : By default `expvar` publishes `cmdline` and `memstats` which is not possible to remove.
         * This PR makes it possible by moving existing function `RemoveAll` from tests
         * <p>
         * Fixes #29105
         * created_at : 2019-10-10T19:42:53Z
         * updated_at : 2019-10-10T19:47:03Z
         * closed_at : null
         * merged_at : null
         * merge_commit_sha : cbebf2e4ae0aad4aac3c546a072cf6531f7b47b7
         * assignee : null
         * assignees : []
         * requested_reviewers : []
         * requested_teams : []
         * labels : [{"id":831707000,"node_id":"MDU6TGFiZWw4MzE3MDcwMDA=","url":"https://api.github.com/repos/golang/go/labels/cla:%20yes","name":"cla: yes","color":"0e8a16","default":false}]
         * milestone : null
         * commits_url : https://api.github.com/repos/golang/go/pulls/34832/commits
         * review_comments_url : https://api.github.com/repos/golang/go/pulls/34832/comments
         * review_comment_url : https://api.github.com/repos/golang/go/pulls/comments{/number}
         * comments_url : https://api.github.com/repos/golang/go/issues/34832/comments
         * statuses_url : https://api.github.com/repos/golang/go/statuses/d132293b705cc89eaffc3d780c687809b67b665e
         * head : {"label":"artemyarulin:expvar-remove-all","ref":"expvar-remove-all","sha":"d132293b705cc89eaffc3d780c687809b67b665e","user":{"login":"artemyarulin","id":6191712,"node_id":"MDQ6VXNlcjYxOTE3MTI=","avatar_url":"https://avatars0.githubusercontent.com/u/6191712?v=4","gravatar_id":"","url":"https://api.github.com/users/artemyarulin","html_url":"https://github.com/artemyarulin","followers_url":"https://api.github.com/users/artemyarulin/followers","following_url":"https://api.github.com/users/artemyarulin/following{/other_user}","gists_url":"https://api.github.com/users/artemyarulin/gists{/gist_id}","starred_url":"https://api.github.com/users/artemyarulin/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/artemyarulin/subscriptions","organizations_url":"https://api.github.com/users/artemyarulin/orgs","repos_url":"https://api.github.com/users/artemyarulin/repos","events_url":"https://api.github.com/users/artemyarulin/events{/privacy}","received_events_url":"https://api.github.com/users/artemyarulin/received_events","type":"User","site_admin":false},"repo":{"id":214267391,"node_id":"MDEwOlJlcG9zaXRvcnkyMTQyNjczOTE=","name":"go","full_name":"artemyarulin/go","private":false,"owner":{"login":"artemyarulin","id":6191712,"node_id":"MDQ6VXNlcjYxOTE3MTI=","avatar_url":"https://avatars0.githubusercontent.com/u/6191712?v=4","gravatar_id":"","url":"https://api.github.com/users/artemyarulin","html_url":"https://github.com/artemyarulin","followers_url":"https://api.github.com/users/artemyarulin/followers","following_url":"https://api.github.com/users/artemyarulin/following{/other_user}","gists_url":"https://api.github.com/users/artemyarulin/gists{/gist_id}","starred_url":"https://api.github.com/users/artemyarulin/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/artemyarulin/subscriptions","organizations_url":"https://api.github.com/users/artemyarulin/orgs","repos_url":"https://api.github.com/users/artemyarulin/repos","events_url":"https://api.github.com/users/artemyarulin/events{/privacy}","received_events_url":"https://api.github.com/users/artemyarulin/received_events","type":"User","site_admin":false},"html_url":"https://github.com/artemyarulin/go","description":"The Go programming language","fork":true,"url":"https://api.github.com/repos/artemyarulin/go","forks_url":"https://api.github.com/repos/artemyarulin/go/forks","keys_url":"https://api.github.com/repos/artemyarulin/go/keys{/key_id}","collaborators_url":"https://api.github.com/repos/artemyarulin/go/collaborators{/collaborator}","teams_url":"https://api.github.com/repos/artemyarulin/go/teams","hooks_url":"https://api.github.com/repos/artemyarulin/go/hooks","issue_events_url":"https://api.github.com/repos/artemyarulin/go/issues/events{/number}","events_url":"https://api.github.com/repos/artemyarulin/go/events","assignees_url":"https://api.github.com/repos/artemyarulin/go/assignees{/user}","branches_url":"https://api.github.com/repos/artemyarulin/go/branches{/branch}","tags_url":"https://api.github.com/repos/artemyarulin/go/tags","blobs_url":"https://api.github.com/repos/artemyarulin/go/git/blobs{/sha}","git_tags_url":"https://api.github.com/repos/artemyarulin/go/git/tags{/sha}","git_refs_url":"https://api.github.com/repos/artemyarulin/go/git/refs{/sha}","trees_url":"https://api.github.com/repos/artemyarulin/go/git/trees{/sha}","statuses_url":"https://api.github.com/repos/artemyarulin/go/statuses/{sha}","languages_url":"https://api.github.com/repos/artemyarulin/go/languages","stargazers_url":"https://api.github.com/repos/artemyarulin/go/stargazers","contributors_url":"https://api.github.com/repos/artemyarulin/go/contributors","subscribers_url":"https://api.github.com/repos/artemyarulin/go/subscribers","subscription_url":"https://api.github.com/repos/artemyarulin/go/subscription","commits_url":"https://api.github.com/repos/artemyarulin/go/commits{/sha}","git_commits_url":"https://api.github.com/repos/artemyarulin/go/git/commits{/sha}","comments_url":"https://api.github.com/repos/artemyarulin/go/comments{/number}","issue_comment_url":"https://api.github.com/repos/artemyarulin/go/issues/comments{/number}","contents_url":"https://api.github.com/repos/artemyarulin/go/contents/{+path}","compare_url":"https://api.github.com/repos/artemyarulin/go/compare/{base}...{head}","merges_url":"https://api.github.com/repos/artemyarulin/go/merges","archive_url":"https://api.github.com/repos/artemyarulin/go/{archive_format}{/ref}","downloads_url":"https://api.github.com/repos/artemyarulin/go/downloads","issues_url":"https://api.github.com/repos/artemyarulin/go/issues{/number}","pulls_url":"https://api.github.com/repos/artemyarulin/go/pulls{/number}","milestones_url":"https://api.github.com/repos/artemyarulin/go/milestones{/number}","notifications_url":"https://api.github.com/repos/artemyarulin/go/notifications{?since,all,participating}","labels_url":"https://api.github.com/repos/artemyarulin/go/labels{/name}","releases_url":"https://api.github.com/repos/artemyarulin/go/releases{/id}","deployments_url":"https://api.github.com/repos/artemyarulin/go/deployments","created_at":"2019-10-10T19:22:32Z","updated_at":"2019-10-10T19:22:34Z","pushed_at":"2019-10-10T19:34:32Z","git_url":"git://github.com/artemyarulin/go.git","ssh_url":"git@github.com:artemyarulin/go.git","clone_url":"https://github.com/artemyarulin/go.git","svn_url":"https://github.com/artemyarulin/go","homepage":"https://golang.org","size":194417,"stargazers_count":0,"watchers_count":0,"language":null,"has_issues":false,"has_projects":true,"has_downloads":true,"has_wiki":true,"has_pages":false,"forks_count":0,"mirror_url":null,"archived":false,"disabled":false,"open_issues_count":0,"license":{"key":"other","name":"Other","spdx_id":"NOASSERTION","url":null,"node_id":"MDc6TGljZW5zZTA="},"forks":0,"open_issues":0,"watchers":0,"default_branch":"master"}}
         * base : {"label":"golang:master","ref":"master","sha":"6dc740f0928e4c1b43697d8e2d4dbc9804911e79","user":{"login":"golang","id":4314092,"node_id":"MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=","avatar_url":"https://avatars3.githubusercontent.com/u/4314092?v=4","gravatar_id":"","url":"https://api.github.com/users/golang","html_url":"https://github.com/golang","followers_url":"https://api.github.com/users/golang/followers","following_url":"https://api.github.com/users/golang/following{/other_user}","gists_url":"https://api.github.com/users/golang/gists{/gist_id}","starred_url":"https://api.github.com/users/golang/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/golang/subscriptions","organizations_url":"https://api.github.com/users/golang/orgs","repos_url":"https://api.github.com/users/golang/repos","events_url":"https://api.github.com/users/golang/events{/privacy}","received_events_url":"https://api.github.com/users/golang/received_events","type":"Organization","site_admin":false},"repo":{"id":23096959,"node_id":"MDEwOlJlcG9zaXRvcnkyMzA5Njk1OQ==","name":"go","full_name":"golang/go","private":false,"owner":{"login":"golang","id":4314092,"node_id":"MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=","avatar_url":"https://avatars3.githubusercontent.com/u/4314092?v=4","gravatar_id":"","url":"https://api.github.com/users/golang","html_url":"https://github.com/golang","followers_url":"https://api.github.com/users/golang/followers","following_url":"https://api.github.com/users/golang/following{/other_user}","gists_url":"https://api.github.com/users/golang/gists{/gist_id}","starred_url":"https://api.github.com/users/golang/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/golang/subscriptions","organizations_url":"https://api.github.com/users/golang/orgs","repos_url":"https://api.github.com/users/golang/repos","events_url":"https://api.github.com/users/golang/events{/privacy}","received_events_url":"https://api.github.com/users/golang/received_events","type":"Organization","site_admin":false},"html_url":"https://github.com/golang/go","description":"The Go programming language","fork":false,"url":"https://api.github.com/repos/golang/go","forks_url":"https://api.github.com/repos/golang/go/forks","keys_url":"https://api.github.com/repos/golang/go/keys{/key_id}","collaborators_url":"https://api.github.com/repos/golang/go/collaborators{/collaborator}","teams_url":"https://api.github.com/repos/golang/go/teams","hooks_url":"https://api.github.com/repos/golang/go/hooks","issue_events_url":"https://api.github.com/repos/golang/go/issues/events{/number}","events_url":"https://api.github.com/repos/golang/go/events","assignees_url":"https://api.github.com/repos/golang/go/assignees{/user}","branches_url":"https://api.github.com/repos/golang/go/branches{/branch}","tags_url":"https://api.github.com/repos/golang/go/tags","blobs_url":"https://api.github.com/repos/golang/go/git/blobs{/sha}","git_tags_url":"https://api.github.com/repos/golang/go/git/tags{/sha}","git_refs_url":"https://api.github.com/repos/golang/go/git/refs{/sha}","trees_url":"https://api.github.com/repos/golang/go/git/trees{/sha}","statuses_url":"https://api.github.com/repos/golang/go/statuses/{sha}","languages_url":"https://api.github.com/repos/golang/go/languages","stargazers_url":"https://api.github.com/repos/golang/go/stargazers","contributors_url":"https://api.github.com/repos/golang/go/contributors","subscribers_url":"https://api.github.com/repos/golang/go/subscribers","subscription_url":"https://api.github.com/repos/golang/go/subscription","commits_url":"https://api.github.com/repos/golang/go/commits{/sha}","git_commits_url":"https://api.github.com/repos/golang/go/git/commits{/sha}","comments_url":"https://api.github.com/repos/golang/go/comments{/number}","issue_comment_url":"https://api.github.com/repos/golang/go/issues/comments{/number}","contents_url":"https://api.github.com/repos/golang/go/contents/{+path}","compare_url":"https://api.github.com/repos/golang/go/compare/{base}...{head}","merges_url":"https://api.github.com/repos/golang/go/merges","archive_url":"https://api.github.com/repos/golang/go/{archive_format}{/ref}","downloads_url":"https://api.github.com/repos/golang/go/downloads","issues_url":"https://api.github.com/repos/golang/go/issues{/number}","pulls_url":"https://api.github.com/repos/golang/go/pulls{/number}","milestones_url":"https://api.github.com/repos/golang/go/milestones{/number}","notifications_url":"https://api.github.com/repos/golang/go/notifications{?since,all,participating}","labels_url":"https://api.github.com/repos/golang/go/labels{/name}","releases_url":"https://api.github.com/repos/golang/go/releases{/id}","deployments_url":"https://api.github.com/repos/golang/go/deployments","created_at":"2014-08-19T04:33:40Z","updated_at":"2019-10-11T09:54:55Z","pushed_at":"2019-10-11T04:02:02Z","git_url":"git://github.com/golang/go.git","ssh_url":"git@github.com:golang/go.git","clone_url":"https://github.com/golang/go.git","svn_url":"https://github.com/golang/go","homepage":"https://golang.org","size":194445,"stargazers_count":64398,"watchers_count":64398,"language":"Go","has_issues":true,"has_projects":false,"has_downloads":true,"has_wiki":true,"has_pages":false,"forks_count":9036,"mirror_url":null,"archived":false,"disabled":false,"open_issues_count":5027,"license":{"key":"other","name":"Other","spdx_id":"NOASSERTION","url":null,"node_id":"MDc6TGljZW5zZTA="},"forks":9036,"open_issues":5027,"watchers":64398,"default_branch":"master"}}
         * _links : {"self":{"href":"https://api.github.com/repos/golang/go/pulls/34832"},"html":{"href":"https://github.com/golang/go/pull/34832"},"issue":{"href":"https://api.github.com/repos/golang/go/issues/34832"},"comments":{"href":"https://api.github.com/repos/golang/go/issues/34832/comments"},"review_comments":{"href":"https://api.github.com/repos/golang/go/pulls/34832/comments"},"review_comment":{"href":"https://api.github.com/repos/golang/go/pulls/comments{/number}"},"commits":{"href":"https://api.github.com/repos/golang/go/pulls/34832/commits"},"statuses":{"href":"https://api.github.com/repos/golang/go/statuses/d132293b705cc89eaffc3d780c687809b67b665e"}}
         * author_association : NONE
         */

        private String url;
        private int id;
        private String node_id;
        private String html_url;
        private String diff_url;
        private String patch_url;
        private String issue_url;
        private int number;
        private String state;
        private boolean locked;
        private String title;
        private UserBean user;
        private String body;
        private String created_at;
        private String updated_at;
        private Object closed_at;
        private Object merged_at;
        private String merge_commit_sha;
        private Object assignee;
        private Object milestone;
        private String commits_url;
        private String review_comments_url;
        private String review_comment_url;
        private String comments_url;
        private String statuses_url;
        private HeadBean head;
        private BaseBean base;
        private LinksBean _links;
        private String author_association;
        private List<?> assignees;
        private List<?> requested_reviewers;
        private List<?> requested_teams;
        private List<LabelsBean> labels;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNode_id() {
            return node_id;
        }

        public void setNode_id(String node_id) {
            this.node_id = node_id;
        }

        public String getHtml_url() {
            return html_url;
        }

        public void setHtml_url(String html_url) {
            this.html_url = html_url;
        }

        public String getDiff_url() {
            return diff_url;
        }

        public void setDiff_url(String diff_url) {
            this.diff_url = diff_url;
        }

        public String getPatch_url() {
            return patch_url;
        }

        public void setPatch_url(String patch_url) {
            this.patch_url = patch_url;
        }

        public String getIssue_url() {
            return issue_url;
        }

        public void setIssue_url(String issue_url) {
            this.issue_url = issue_url;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public Object getClosed_at() {
            return closed_at;
        }

        public void setClosed_at(Object closed_at) {
            this.closed_at = closed_at;
        }

        public Object getMerged_at() {
            return merged_at;
        }

        public void setMerged_at(Object merged_at) {
            this.merged_at = merged_at;
        }

        public String getMerge_commit_sha() {
            return merge_commit_sha;
        }

        public void setMerge_commit_sha(String merge_commit_sha) {
            this.merge_commit_sha = merge_commit_sha;
        }

        public Object getAssignee() {
            return assignee;
        }

        public void setAssignee(Object assignee) {
            this.assignee = assignee;
        }

        public Object getMilestone() {
            return milestone;
        }

        public void setMilestone(Object milestone) {
            this.milestone = milestone;
        }

        public String getCommits_url() {
            return commits_url;
        }

        public void setCommits_url(String commits_url) {
            this.commits_url = commits_url;
        }

        public String getReview_comments_url() {
            return review_comments_url;
        }

        public void setReview_comments_url(String review_comments_url) {
            this.review_comments_url = review_comments_url;
        }

        public String getReview_comment_url() {
            return review_comment_url;
        }

        public void setReview_comment_url(String review_comment_url) {
            this.review_comment_url = review_comment_url;
        }

        public String getComments_url() {
            return comments_url;
        }

        public void setComments_url(String comments_url) {
            this.comments_url = comments_url;
        }

        public String getStatuses_url() {
            return statuses_url;
        }

        public void setStatuses_url(String statuses_url) {
            this.statuses_url = statuses_url;
        }

        public HeadBean getHead() {
            return head;
        }

        public void setHead(HeadBean head) {
            this.head = head;
        }

        public BaseBean getBase() {
            return base;
        }

        public void setBase(BaseBean base) {
            this.base = base;
        }

        public LinksBean get_links() {
            return _links;
        }

        public void set_links(LinksBean _links) {
            this._links = _links;
        }

        public String getAuthor_association() {
            return author_association;
        }

        public void setAuthor_association(String author_association) {
            this.author_association = author_association;
        }

        public List<?> getAssignees() {
            return assignees;
        }

        public void setAssignees(List<?> assignees) {
            this.assignees = assignees;
        }

        public List<?> getRequested_reviewers() {
            return requested_reviewers;
        }

        public void setRequested_reviewers(List<?> requested_reviewers) {
            this.requested_reviewers = requested_reviewers;
        }

        public List<?> getRequested_teams() {
            return requested_teams;
        }

        public void setRequested_teams(List<?> requested_teams) {
            this.requested_teams = requested_teams;
        }

        public List<LabelsBean> getLabels() {
            return labels;
        }

        public void setLabels(List<LabelsBean> labels) {
            this.labels = labels;
        }

        public static class UserBean {
            /**
             * login : artemyarulin
             * id : 6191712
             * node_id : MDQ6VXNlcjYxOTE3MTI=
             * avatar_url : https://avatars0.githubusercontent.com/u/6191712?v=4
             * gravatar_id :
             * url : https://api.github.com/users/artemyarulin
             * html_url : https://github.com/artemyarulin
             * followers_url : https://api.github.com/users/artemyarulin/followers
             * following_url : https://api.github.com/users/artemyarulin/following{/other_user}
             * gists_url : https://api.github.com/users/artemyarulin/gists{/gist_id}
             * starred_url : https://api.github.com/users/artemyarulin/starred{/owner}{/repo}
             * subscriptions_url : https://api.github.com/users/artemyarulin/subscriptions
             * organizations_url : https://api.github.com/users/artemyarulin/orgs
             * repos_url : https://api.github.com/users/artemyarulin/repos
             * events_url : https://api.github.com/users/artemyarulin/events{/privacy}
             * received_events_url : https://api.github.com/users/artemyarulin/received_events
             * type : User
             * site_admin : false
             */

            private String login;
            private int id;
            private String node_id;
            private String avatar_url;
            private String gravatar_id;
            private String url;
            private String html_url;
            private String followers_url;
            private String following_url;
            private String gists_url;
            private String starred_url;
            private String subscriptions_url;
            private String organizations_url;
            private String repos_url;
            private String events_url;
            private String received_events_url;
            private String type;
            private boolean site_admin;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNode_id() {
                return node_id;
            }

            public void setNode_id(String node_id) {
                this.node_id = node_id;
            }

            public String getAvatar_url() {
                return avatar_url;
            }

            public void setAvatar_url(String avatar_url) {
                this.avatar_url = avatar_url;
            }

            public String getGravatar_id() {
                return gravatar_id;
            }

            public void setGravatar_id(String gravatar_id) {
                this.gravatar_id = gravatar_id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getHtml_url() {
                return html_url;
            }

            public void setHtml_url(String html_url) {
                this.html_url = html_url;
            }

            public String getFollowers_url() {
                return followers_url;
            }

            public void setFollowers_url(String followers_url) {
                this.followers_url = followers_url;
            }

            public String getFollowing_url() {
                return following_url;
            }

            public void setFollowing_url(String following_url) {
                this.following_url = following_url;
            }

            public String getGists_url() {
                return gists_url;
            }

            public void setGists_url(String gists_url) {
                this.gists_url = gists_url;
            }

            public String getStarred_url() {
                return starred_url;
            }

            public void setStarred_url(String starred_url) {
                this.starred_url = starred_url;
            }

            public String getSubscriptions_url() {
                return subscriptions_url;
            }

            public void setSubscriptions_url(String subscriptions_url) {
                this.subscriptions_url = subscriptions_url;
            }

            public String getOrganizations_url() {
                return organizations_url;
            }

            public void setOrganizations_url(String organizations_url) {
                this.organizations_url = organizations_url;
            }

            public String getRepos_url() {
                return repos_url;
            }

            public void setRepos_url(String repos_url) {
                this.repos_url = repos_url;
            }

            public String getEvents_url() {
                return events_url;
            }

            public void setEvents_url(String events_url) {
                this.events_url = events_url;
            }

            public String getReceived_events_url() {
                return received_events_url;
            }

            public void setReceived_events_url(String received_events_url) {
                this.received_events_url = received_events_url;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public boolean isSite_admin() {
                return site_admin;
            }

            public void setSite_admin(boolean site_admin) {
                this.site_admin = site_admin;
            }
        }

        public static class HeadBean {
            /**
             * label : artemyarulin:expvar-remove-all
             * ref : expvar-remove-all
             * sha : d132293b705cc89eaffc3d780c687809b67b665e
             * user : {"login":"artemyarulin","id":6191712,"node_id":"MDQ6VXNlcjYxOTE3MTI=","avatar_url":"https://avatars0.githubusercontent.com/u/6191712?v=4","gravatar_id":"","url":"https://api.github.com/users/artemyarulin","html_url":"https://github.com/artemyarulin","followers_url":"https://api.github.com/users/artemyarulin/followers","following_url":"https://api.github.com/users/artemyarulin/following{/other_user}","gists_url":"https://api.github.com/users/artemyarulin/gists{/gist_id}","starred_url":"https://api.github.com/users/artemyarulin/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/artemyarulin/subscriptions","organizations_url":"https://api.github.com/users/artemyarulin/orgs","repos_url":"https://api.github.com/users/artemyarulin/repos","events_url":"https://api.github.com/users/artemyarulin/events{/privacy}","received_events_url":"https://api.github.com/users/artemyarulin/received_events","type":"User","site_admin":false}
             * repo : {"id":214267391,"node_id":"MDEwOlJlcG9zaXRvcnkyMTQyNjczOTE=","name":"go","full_name":"artemyarulin/go","private":false,"owner":{"login":"artemyarulin","id":6191712,"node_id":"MDQ6VXNlcjYxOTE3MTI=","avatar_url":"https://avatars0.githubusercontent.com/u/6191712?v=4","gravatar_id":"","url":"https://api.github.com/users/artemyarulin","html_url":"https://github.com/artemyarulin","followers_url":"https://api.github.com/users/artemyarulin/followers","following_url":"https://api.github.com/users/artemyarulin/following{/other_user}","gists_url":"https://api.github.com/users/artemyarulin/gists{/gist_id}","starred_url":"https://api.github.com/users/artemyarulin/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/artemyarulin/subscriptions","organizations_url":"https://api.github.com/users/artemyarulin/orgs","repos_url":"https://api.github.com/users/artemyarulin/repos","events_url":"https://api.github.com/users/artemyarulin/events{/privacy}","received_events_url":"https://api.github.com/users/artemyarulin/received_events","type":"User","site_admin":false},"html_url":"https://github.com/artemyarulin/go","description":"The Go programming language","fork":true,"url":"https://api.github.com/repos/artemyarulin/go","forks_url":"https://api.github.com/repos/artemyarulin/go/forks","keys_url":"https://api.github.com/repos/artemyarulin/go/keys{/key_id}","collaborators_url":"https://api.github.com/repos/artemyarulin/go/collaborators{/collaborator}","teams_url":"https://api.github.com/repos/artemyarulin/go/teams","hooks_url":"https://api.github.com/repos/artemyarulin/go/hooks","issue_events_url":"https://api.github.com/repos/artemyarulin/go/issues/events{/number}","events_url":"https://api.github.com/repos/artemyarulin/go/events","assignees_url":"https://api.github.com/repos/artemyarulin/go/assignees{/user}","branches_url":"https://api.github.com/repos/artemyarulin/go/branches{/branch}","tags_url":"https://api.github.com/repos/artemyarulin/go/tags","blobs_url":"https://api.github.com/repos/artemyarulin/go/git/blobs{/sha}","git_tags_url":"https://api.github.com/repos/artemyarulin/go/git/tags{/sha}","git_refs_url":"https://api.github.com/repos/artemyarulin/go/git/refs{/sha}","trees_url":"https://api.github.com/repos/artemyarulin/go/git/trees{/sha}","statuses_url":"https://api.github.com/repos/artemyarulin/go/statuses/{sha}","languages_url":"https://api.github.com/repos/artemyarulin/go/languages","stargazers_url":"https://api.github.com/repos/artemyarulin/go/stargazers","contributors_url":"https://api.github.com/repos/artemyarulin/go/contributors","subscribers_url":"https://api.github.com/repos/artemyarulin/go/subscribers","subscription_url":"https://api.github.com/repos/artemyarulin/go/subscription","commits_url":"https://api.github.com/repos/artemyarulin/go/commits{/sha}","git_commits_url":"https://api.github.com/repos/artemyarulin/go/git/commits{/sha}","comments_url":"https://api.github.com/repos/artemyarulin/go/comments{/number}","issue_comment_url":"https://api.github.com/repos/artemyarulin/go/issues/comments{/number}","contents_url":"https://api.github.com/repos/artemyarulin/go/contents/{+path}","compare_url":"https://api.github.com/repos/artemyarulin/go/compare/{base}...{head}","merges_url":"https://api.github.com/repos/artemyarulin/go/merges","archive_url":"https://api.github.com/repos/artemyarulin/go/{archive_format}{/ref}","downloads_url":"https://api.github.com/repos/artemyarulin/go/downloads","issues_url":"https://api.github.com/repos/artemyarulin/go/issues{/number}","pulls_url":"https://api.github.com/repos/artemyarulin/go/pulls{/number}","milestones_url":"https://api.github.com/repos/artemyarulin/go/milestones{/number}","notifications_url":"https://api.github.com/repos/artemyarulin/go/notifications{?since,all,participating}","labels_url":"https://api.github.com/repos/artemyarulin/go/labels{/name}","releases_url":"https://api.github.com/repos/artemyarulin/go/releases{/id}","deployments_url":"https://api.github.com/repos/artemyarulin/go/deployments","created_at":"2019-10-10T19:22:32Z","updated_at":"2019-10-10T19:22:34Z","pushed_at":"2019-10-10T19:34:32Z","git_url":"git://github.com/artemyarulin/go.git","ssh_url":"git@github.com:artemyarulin/go.git","clone_url":"https://github.com/artemyarulin/go.git","svn_url":"https://github.com/artemyarulin/go","homepage":"https://golang.org","size":194417,"stargazers_count":0,"watchers_count":0,"language":null,"has_issues":false,"has_projects":true,"has_downloads":true,"has_wiki":true,"has_pages":false,"forks_count":0,"mirror_url":null,"archived":false,"disabled":false,"open_issues_count":0,"license":{"key":"other","name":"Other","spdx_id":"NOASSERTION","url":null,"node_id":"MDc6TGljZW5zZTA="},"forks":0,"open_issues":0,"watchers":0,"default_branch":"master"}
             */

            private String label;
            private String ref;
            private String sha;
            private UserBeanX user;
            private RepoBean repo;

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public String getRef() {
                return ref;
            }

            public void setRef(String ref) {
                this.ref = ref;
            }

            public String getSha() {
                return sha;
            }

            public void setSha(String sha) {
                this.sha = sha;
            }

            public UserBeanX getUser() {
                return user;
            }

            public void setUser(UserBeanX user) {
                this.user = user;
            }

            public RepoBean getRepo() {
                return repo;
            }

            public void setRepo(RepoBean repo) {
                this.repo = repo;
            }

            public static class UserBeanX {
                /**
                 * login : artemyarulin
                 * id : 6191712
                 * node_id : MDQ6VXNlcjYxOTE3MTI=
                 * avatar_url : https://avatars0.githubusercontent.com/u/6191712?v=4
                 * gravatar_id :
                 * url : https://api.github.com/users/artemyarulin
                 * html_url : https://github.com/artemyarulin
                 * followers_url : https://api.github.com/users/artemyarulin/followers
                 * following_url : https://api.github.com/users/artemyarulin/following{/other_user}
                 * gists_url : https://api.github.com/users/artemyarulin/gists{/gist_id}
                 * starred_url : https://api.github.com/users/artemyarulin/starred{/owner}{/repo}
                 * subscriptions_url : https://api.github.com/users/artemyarulin/subscriptions
                 * organizations_url : https://api.github.com/users/artemyarulin/orgs
                 * repos_url : https://api.github.com/users/artemyarulin/repos
                 * events_url : https://api.github.com/users/artemyarulin/events{/privacy}
                 * received_events_url : https://api.github.com/users/artemyarulin/received_events
                 * type : User
                 * site_admin : false
                 */

                private String login;
                private int id;
                private String node_id;
                private String avatar_url;
                private String gravatar_id;
                private String url;
                private String html_url;
                private String followers_url;
                private String following_url;
                private String gists_url;
                private String starred_url;
                private String subscriptions_url;
                private String organizations_url;
                private String repos_url;
                private String events_url;
                private String received_events_url;
                private String type;
                private boolean site_admin;

                public String getLogin() {
                    return login;
                }

                public void setLogin(String login) {
                    this.login = login;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getNode_id() {
                    return node_id;
                }

                public void setNode_id(String node_id) {
                    this.node_id = node_id;
                }

                public String getAvatar_url() {
                    return avatar_url;
                }

                public void setAvatar_url(String avatar_url) {
                    this.avatar_url = avatar_url;
                }

                public String getGravatar_id() {
                    return gravatar_id;
                }

                public void setGravatar_id(String gravatar_id) {
                    this.gravatar_id = gravatar_id;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getHtml_url() {
                    return html_url;
                }

                public void setHtml_url(String html_url) {
                    this.html_url = html_url;
                }

                public String getFollowers_url() {
                    return followers_url;
                }

                public void setFollowers_url(String followers_url) {
                    this.followers_url = followers_url;
                }

                public String getFollowing_url() {
                    return following_url;
                }

                public void setFollowing_url(String following_url) {
                    this.following_url = following_url;
                }

                public String getGists_url() {
                    return gists_url;
                }

                public void setGists_url(String gists_url) {
                    this.gists_url = gists_url;
                }

                public String getStarred_url() {
                    return starred_url;
                }

                public void setStarred_url(String starred_url) {
                    this.starred_url = starred_url;
                }

                public String getSubscriptions_url() {
                    return subscriptions_url;
                }

                public void setSubscriptions_url(String subscriptions_url) {
                    this.subscriptions_url = subscriptions_url;
                }

                public String getOrganizations_url() {
                    return organizations_url;
                }

                public void setOrganizations_url(String organizations_url) {
                    this.organizations_url = organizations_url;
                }

                public String getRepos_url() {
                    return repos_url;
                }

                public void setRepos_url(String repos_url) {
                    this.repos_url = repos_url;
                }

                public String getEvents_url() {
                    return events_url;
                }

                public void setEvents_url(String events_url) {
                    this.events_url = events_url;
                }

                public String getReceived_events_url() {
                    return received_events_url;
                }

                public void setReceived_events_url(String received_events_url) {
                    this.received_events_url = received_events_url;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public boolean isSite_admin() {
                    return site_admin;
                }

                public void setSite_admin(boolean site_admin) {
                    this.site_admin = site_admin;
                }
            }

            public static class RepoBean {
                /**
                 * id : 214267391
                 * node_id : MDEwOlJlcG9zaXRvcnkyMTQyNjczOTE=
                 * name : go
                 * full_name : artemyarulin/go
                 * private : false
                 * owner : {"login":"artemyarulin","id":6191712,"node_id":"MDQ6VXNlcjYxOTE3MTI=","avatar_url":"https://avatars0.githubusercontent.com/u/6191712?v=4","gravatar_id":"","url":"https://api.github.com/users/artemyarulin","html_url":"https://github.com/artemyarulin","followers_url":"https://api.github.com/users/artemyarulin/followers","following_url":"https://api.github.com/users/artemyarulin/following{/other_user}","gists_url":"https://api.github.com/users/artemyarulin/gists{/gist_id}","starred_url":"https://api.github.com/users/artemyarulin/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/artemyarulin/subscriptions","organizations_url":"https://api.github.com/users/artemyarulin/orgs","repos_url":"https://api.github.com/users/artemyarulin/repos","events_url":"https://api.github.com/users/artemyarulin/events{/privacy}","received_events_url":"https://api.github.com/users/artemyarulin/received_events","type":"User","site_admin":false}
                 * html_url : https://github.com/artemyarulin/go
                 * description : The Go programming language
                 * fork : true
                 * url : https://api.github.com/repos/artemyarulin/go
                 * forks_url : https://api.github.com/repos/artemyarulin/go/forks
                 * keys_url : https://api.github.com/repos/artemyarulin/go/keys{/key_id}
                 * collaborators_url : https://api.github.com/repos/artemyarulin/go/collaborators{/collaborator}
                 * teams_url : https://api.github.com/repos/artemyarulin/go/teams
                 * hooks_url : https://api.github.com/repos/artemyarulin/go/hooks
                 * issue_events_url : https://api.github.com/repos/artemyarulin/go/issues/events{/number}
                 * events_url : https://api.github.com/repos/artemyarulin/go/events
                 * assignees_url : https://api.github.com/repos/artemyarulin/go/assignees{/user}
                 * branches_url : https://api.github.com/repos/artemyarulin/go/branches{/branch}
                 * tags_url : https://api.github.com/repos/artemyarulin/go/tags
                 * blobs_url : https://api.github.com/repos/artemyarulin/go/git/blobs{/sha}
                 * git_tags_url : https://api.github.com/repos/artemyarulin/go/git/tags{/sha}
                 * git_refs_url : https://api.github.com/repos/artemyarulin/go/git/refs{/sha}
                 * trees_url : https://api.github.com/repos/artemyarulin/go/git/trees{/sha}
                 * statuses_url : https://api.github.com/repos/artemyarulin/go/statuses/{sha}
                 * languages_url : https://api.github.com/repos/artemyarulin/go/languages
                 * stargazers_url : https://api.github.com/repos/artemyarulin/go/stargazers
                 * contributors_url : https://api.github.com/repos/artemyarulin/go/contributors
                 * subscribers_url : https://api.github.com/repos/artemyarulin/go/subscribers
                 * subscription_url : https://api.github.com/repos/artemyarulin/go/subscription
                 * commits_url : https://api.github.com/repos/artemyarulin/go/commits{/sha}
                 * git_commits_url : https://api.github.com/repos/artemyarulin/go/git/commits{/sha}
                 * comments_url : https://api.github.com/repos/artemyarulin/go/comments{/number}
                 * issue_comment_url : https://api.github.com/repos/artemyarulin/go/issues/comments{/number}
                 * contents_url : https://api.github.com/repos/artemyarulin/go/contents/{+path}
                 * compare_url : https://api.github.com/repos/artemyarulin/go/compare/{base}...{head}
                 * merges_url : https://api.github.com/repos/artemyarulin/go/merges
                 * archive_url : https://api.github.com/repos/artemyarulin/go/{archive_format}{/ref}
                 * downloads_url : https://api.github.com/repos/artemyarulin/go/downloads
                 * issues_url : https://api.github.com/repos/artemyarulin/go/issues{/number}
                 * pulls_url : https://api.github.com/repos/artemyarulin/go/pulls{/number}
                 * milestones_url : https://api.github.com/repos/artemyarulin/go/milestones{/number}
                 * notifications_url : https://api.github.com/repos/artemyarulin/go/notifications{?since,all,participating}
                 * labels_url : https://api.github.com/repos/artemyarulin/go/labels{/name}
                 * releases_url : https://api.github.com/repos/artemyarulin/go/releases{/id}
                 * deployments_url : https://api.github.com/repos/artemyarulin/go/deployments
                 * created_at : 2019-10-10T19:22:32Z
                 * updated_at : 2019-10-10T19:22:34Z
                 * pushed_at : 2019-10-10T19:34:32Z
                 * git_url : git://github.com/artemyarulin/go.git
                 * ssh_url : git@github.com:artemyarulin/go.git
                 * clone_url : https://github.com/artemyarulin/go.git
                 * svn_url : https://github.com/artemyarulin/go
                 * homepage : https://golang.org
                 * size : 194417
                 * stargazers_count : 0
                 * watchers_count : 0
                 * language : null
                 * has_issues : false
                 * has_projects : true
                 * has_downloads : true
                 * has_wiki : true
                 * has_pages : false
                 * forks_count : 0
                 * mirror_url : null
                 * archived : false
                 * disabled : false
                 * open_issues_count : 0
                 * license : {"key":"other","name":"Other","spdx_id":"NOASSERTION","url":null,"node_id":"MDc6TGljZW5zZTA="}
                 * forks : 0
                 * open_issues : 0
                 * watchers : 0
                 * default_branch : master
                 */

                private int id;
                private String node_id;
                private String name;
                private String full_name;
                @com.google.gson.annotations.SerializedName("private")
                private boolean privateX;
                private OwnerBean owner;
                private String html_url;
                private String description;
                private boolean fork;
                private String url;
                private String forks_url;
                private String keys_url;
                private String collaborators_url;
                private String teams_url;
                private String hooks_url;
                private String issue_events_url;
                private String events_url;
                private String assignees_url;
                private String branches_url;
                private String tags_url;
                private String blobs_url;
                private String git_tags_url;
                private String git_refs_url;
                private String trees_url;
                private String statuses_url;
                private String languages_url;
                private String stargazers_url;
                private String contributors_url;
                private String subscribers_url;
                private String subscription_url;
                private String commits_url;
                private String git_commits_url;
                private String comments_url;
                private String issue_comment_url;
                private String contents_url;
                private String compare_url;
                private String merges_url;
                private String archive_url;
                private String downloads_url;
                private String issues_url;
                private String pulls_url;
                private String milestones_url;
                private String notifications_url;
                private String labels_url;
                private String releases_url;
                private String deployments_url;
                private String created_at;
                private String updated_at;
                private String pushed_at;
                private String git_url;
                private String ssh_url;
                private String clone_url;
                private String svn_url;
                private String homepage;
                private int size;
                private int stargazers_count;
                private int watchers_count;
                private Object language;
                private boolean has_issues;
                private boolean has_projects;
                private boolean has_downloads;
                private boolean has_wiki;
                private boolean has_pages;
                private int forks_count;
                private Object mirror_url;
                private boolean archived;
                private boolean disabled;
                private int open_issues_count;
                private LicenseBean license;
                private int forks;
                private int open_issues;
                private int watchers;
                private String default_branch;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getNode_id() {
                    return node_id;
                }

                public void setNode_id(String node_id) {
                    this.node_id = node_id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getFull_name() {
                    return full_name;
                }

                public void setFull_name(String full_name) {
                    this.full_name = full_name;
                }

                public boolean isPrivateX() {
                    return privateX;
                }

                public void setPrivateX(boolean privateX) {
                    this.privateX = privateX;
                }

                public OwnerBean getOwner() {
                    return owner;
                }

                public void setOwner(OwnerBean owner) {
                    this.owner = owner;
                }

                public String getHtml_url() {
                    return html_url;
                }

                public void setHtml_url(String html_url) {
                    this.html_url = html_url;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public boolean isFork() {
                    return fork;
                }

                public void setFork(boolean fork) {
                    this.fork = fork;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getForks_url() {
                    return forks_url;
                }

                public void setForks_url(String forks_url) {
                    this.forks_url = forks_url;
                }

                public String getKeys_url() {
                    return keys_url;
                }

                public void setKeys_url(String keys_url) {
                    this.keys_url = keys_url;
                }

                public String getCollaborators_url() {
                    return collaborators_url;
                }

                public void setCollaborators_url(String collaborators_url) {
                    this.collaborators_url = collaborators_url;
                }

                public String getTeams_url() {
                    return teams_url;
                }

                public void setTeams_url(String teams_url) {
                    this.teams_url = teams_url;
                }

                public String getHooks_url() {
                    return hooks_url;
                }

                public void setHooks_url(String hooks_url) {
                    this.hooks_url = hooks_url;
                }

                public String getIssue_events_url() {
                    return issue_events_url;
                }

                public void setIssue_events_url(String issue_events_url) {
                    this.issue_events_url = issue_events_url;
                }

                public String getEvents_url() {
                    return events_url;
                }

                public void setEvents_url(String events_url) {
                    this.events_url = events_url;
                }

                public String getAssignees_url() {
                    return assignees_url;
                }

                public void setAssignees_url(String assignees_url) {
                    this.assignees_url = assignees_url;
                }

                public String getBranches_url() {
                    return branches_url;
                }

                public void setBranches_url(String branches_url) {
                    this.branches_url = branches_url;
                }

                public String getTags_url() {
                    return tags_url;
                }

                public void setTags_url(String tags_url) {
                    this.tags_url = tags_url;
                }

                public String getBlobs_url() {
                    return blobs_url;
                }

                public void setBlobs_url(String blobs_url) {
                    this.blobs_url = blobs_url;
                }

                public String getGit_tags_url() {
                    return git_tags_url;
                }

                public void setGit_tags_url(String git_tags_url) {
                    this.git_tags_url = git_tags_url;
                }

                public String getGit_refs_url() {
                    return git_refs_url;
                }

                public void setGit_refs_url(String git_refs_url) {
                    this.git_refs_url = git_refs_url;
                }

                public String getTrees_url() {
                    return trees_url;
                }

                public void setTrees_url(String trees_url) {
                    this.trees_url = trees_url;
                }

                public String getStatuses_url() {
                    return statuses_url;
                }

                public void setStatuses_url(String statuses_url) {
                    this.statuses_url = statuses_url;
                }

                public String getLanguages_url() {
                    return languages_url;
                }

                public void setLanguages_url(String languages_url) {
                    this.languages_url = languages_url;
                }

                public String getStargazers_url() {
                    return stargazers_url;
                }

                public void setStargazers_url(String stargazers_url) {
                    this.stargazers_url = stargazers_url;
                }

                public String getContributors_url() {
                    return contributors_url;
                }

                public void setContributors_url(String contributors_url) {
                    this.contributors_url = contributors_url;
                }

                public String getSubscribers_url() {
                    return subscribers_url;
                }

                public void setSubscribers_url(String subscribers_url) {
                    this.subscribers_url = subscribers_url;
                }

                public String getSubscription_url() {
                    return subscription_url;
                }

                public void setSubscription_url(String subscription_url) {
                    this.subscription_url = subscription_url;
                }

                public String getCommits_url() {
                    return commits_url;
                }

                public void setCommits_url(String commits_url) {
                    this.commits_url = commits_url;
                }

                public String getGit_commits_url() {
                    return git_commits_url;
                }

                public void setGit_commits_url(String git_commits_url) {
                    this.git_commits_url = git_commits_url;
                }

                public String getComments_url() {
                    return comments_url;
                }

                public void setComments_url(String comments_url) {
                    this.comments_url = comments_url;
                }

                public String getIssue_comment_url() {
                    return issue_comment_url;
                }

                public void setIssue_comment_url(String issue_comment_url) {
                    this.issue_comment_url = issue_comment_url;
                }

                public String getContents_url() {
                    return contents_url;
                }

                public void setContents_url(String contents_url) {
                    this.contents_url = contents_url;
                }

                public String getCompare_url() {
                    return compare_url;
                }

                public void setCompare_url(String compare_url) {
                    this.compare_url = compare_url;
                }

                public String getMerges_url() {
                    return merges_url;
                }

                public void setMerges_url(String merges_url) {
                    this.merges_url = merges_url;
                }

                public String getArchive_url() {
                    return archive_url;
                }

                public void setArchive_url(String archive_url) {
                    this.archive_url = archive_url;
                }

                public String getDownloads_url() {
                    return downloads_url;
                }

                public void setDownloads_url(String downloads_url) {
                    this.downloads_url = downloads_url;
                }

                public String getIssues_url() {
                    return issues_url;
                }

                public void setIssues_url(String issues_url) {
                    this.issues_url = issues_url;
                }

                public String getPulls_url() {
                    return pulls_url;
                }

                public void setPulls_url(String pulls_url) {
                    this.pulls_url = pulls_url;
                }

                public String getMilestones_url() {
                    return milestones_url;
                }

                public void setMilestones_url(String milestones_url) {
                    this.milestones_url = milestones_url;
                }

                public String getNotifications_url() {
                    return notifications_url;
                }

                public void setNotifications_url(String notifications_url) {
                    this.notifications_url = notifications_url;
                }

                public String getLabels_url() {
                    return labels_url;
                }

                public void setLabels_url(String labels_url) {
                    this.labels_url = labels_url;
                }

                public String getReleases_url() {
                    return releases_url;
                }

                public void setReleases_url(String releases_url) {
                    this.releases_url = releases_url;
                }

                public String getDeployments_url() {
                    return deployments_url;
                }

                public void setDeployments_url(String deployments_url) {
                    this.deployments_url = deployments_url;
                }

                public String getCreated_at() {
                    return created_at;
                }

                public void setCreated_at(String created_at) {
                    this.created_at = created_at;
                }

                public String getUpdated_at() {
                    return updated_at;
                }

                public void setUpdated_at(String updated_at) {
                    this.updated_at = updated_at;
                }

                public String getPushed_at() {
                    return pushed_at;
                }

                public void setPushed_at(String pushed_at) {
                    this.pushed_at = pushed_at;
                }

                public String getGit_url() {
                    return git_url;
                }

                public void setGit_url(String git_url) {
                    this.git_url = git_url;
                }

                public String getSsh_url() {
                    return ssh_url;
                }

                public void setSsh_url(String ssh_url) {
                    this.ssh_url = ssh_url;
                }

                public String getClone_url() {
                    return clone_url;
                }

                public void setClone_url(String clone_url) {
                    this.clone_url = clone_url;
                }

                public String getSvn_url() {
                    return svn_url;
                }

                public void setSvn_url(String svn_url) {
                    this.svn_url = svn_url;
                }

                public String getHomepage() {
                    return homepage;
                }

                public void setHomepage(String homepage) {
                    this.homepage = homepage;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public int getStargazers_count() {
                    return stargazers_count;
                }

                public void setStargazers_count(int stargazers_count) {
                    this.stargazers_count = stargazers_count;
                }

                public int getWatchers_count() {
                    return watchers_count;
                }

                public void setWatchers_count(int watchers_count) {
                    this.watchers_count = watchers_count;
                }

                public Object getLanguage() {
                    return language;
                }

                public void setLanguage(Object language) {
                    this.language = language;
                }

                public boolean isHas_issues() {
                    return has_issues;
                }

                public void setHas_issues(boolean has_issues) {
                    this.has_issues = has_issues;
                }

                public boolean isHas_projects() {
                    return has_projects;
                }

                public void setHas_projects(boolean has_projects) {
                    this.has_projects = has_projects;
                }

                public boolean isHas_downloads() {
                    return has_downloads;
                }

                public void setHas_downloads(boolean has_downloads) {
                    this.has_downloads = has_downloads;
                }

                public boolean isHas_wiki() {
                    return has_wiki;
                }

                public void setHas_wiki(boolean has_wiki) {
                    this.has_wiki = has_wiki;
                }

                public boolean isHas_pages() {
                    return has_pages;
                }

                public void setHas_pages(boolean has_pages) {
                    this.has_pages = has_pages;
                }

                public int getForks_count() {
                    return forks_count;
                }

                public void setForks_count(int forks_count) {
                    this.forks_count = forks_count;
                }

                public Object getMirror_url() {
                    return mirror_url;
                }

                public void setMirror_url(Object mirror_url) {
                    this.mirror_url = mirror_url;
                }

                public boolean isArchived() {
                    return archived;
                }

                public void setArchived(boolean archived) {
                    this.archived = archived;
                }

                public boolean isDisabled() {
                    return disabled;
                }

                public void setDisabled(boolean disabled) {
                    this.disabled = disabled;
                }

                public int getOpen_issues_count() {
                    return open_issues_count;
                }

                public void setOpen_issues_count(int open_issues_count) {
                    this.open_issues_count = open_issues_count;
                }

                public LicenseBean getLicense() {
                    return license;
                }

                public void setLicense(LicenseBean license) {
                    this.license = license;
                }

                public int getForks() {
                    return forks;
                }

                public void setForks(int forks) {
                    this.forks = forks;
                }

                public int getOpen_issues() {
                    return open_issues;
                }

                public void setOpen_issues(int open_issues) {
                    this.open_issues = open_issues;
                }

                public int getWatchers() {
                    return watchers;
                }

                public void setWatchers(int watchers) {
                    this.watchers = watchers;
                }

                public String getDefault_branch() {
                    return default_branch;
                }

                public void setDefault_branch(String default_branch) {
                    this.default_branch = default_branch;
                }

                public static class OwnerBean {
                    /**
                     * login : artemyarulin
                     * id : 6191712
                     * node_id : MDQ6VXNlcjYxOTE3MTI=
                     * avatar_url : https://avatars0.githubusercontent.com/u/6191712?v=4
                     * gravatar_id :
                     * url : https://api.github.com/users/artemyarulin
                     * html_url : https://github.com/artemyarulin
                     * followers_url : https://api.github.com/users/artemyarulin/followers
                     * following_url : https://api.github.com/users/artemyarulin/following{/other_user}
                     * gists_url : https://api.github.com/users/artemyarulin/gists{/gist_id}
                     * starred_url : https://api.github.com/users/artemyarulin/starred{/owner}{/repo}
                     * subscriptions_url : https://api.github.com/users/artemyarulin/subscriptions
                     * organizations_url : https://api.github.com/users/artemyarulin/orgs
                     * repos_url : https://api.github.com/users/artemyarulin/repos
                     * events_url : https://api.github.com/users/artemyarulin/events{/privacy}
                     * received_events_url : https://api.github.com/users/artemyarulin/received_events
                     * type : User
                     * site_admin : false
                     */

                    private String login;
                    private int id;
                    private String node_id;
                    private String avatar_url;
                    private String gravatar_id;
                    private String url;
                    private String html_url;
                    private String followers_url;
                    private String following_url;
                    private String gists_url;
                    private String starred_url;
                    private String subscriptions_url;
                    private String organizations_url;
                    private String repos_url;
                    private String events_url;
                    private String received_events_url;
                    private String type;
                    private boolean site_admin;

                    public String getLogin() {
                        return login;
                    }

                    public void setLogin(String login) {
                        this.login = login;
                    }

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public String getNode_id() {
                        return node_id;
                    }

                    public void setNode_id(String node_id) {
                        this.node_id = node_id;
                    }

                    public String getAvatar_url() {
                        return avatar_url;
                    }

                    public void setAvatar_url(String avatar_url) {
                        this.avatar_url = avatar_url;
                    }

                    public String getGravatar_id() {
                        return gravatar_id;
                    }

                    public void setGravatar_id(String gravatar_id) {
                        this.gravatar_id = gravatar_id;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getHtml_url() {
                        return html_url;
                    }

                    public void setHtml_url(String html_url) {
                        this.html_url = html_url;
                    }

                    public String getFollowers_url() {
                        return followers_url;
                    }

                    public void setFollowers_url(String followers_url) {
                        this.followers_url = followers_url;
                    }

                    public String getFollowing_url() {
                        return following_url;
                    }

                    public void setFollowing_url(String following_url) {
                        this.following_url = following_url;
                    }

                    public String getGists_url() {
                        return gists_url;
                    }

                    public void setGists_url(String gists_url) {
                        this.gists_url = gists_url;
                    }

                    public String getStarred_url() {
                        return starred_url;
                    }

                    public void setStarred_url(String starred_url) {
                        this.starred_url = starred_url;
                    }

                    public String getSubscriptions_url() {
                        return subscriptions_url;
                    }

                    public void setSubscriptions_url(String subscriptions_url) {
                        this.subscriptions_url = subscriptions_url;
                    }

                    public String getOrganizations_url() {
                        return organizations_url;
                    }

                    public void setOrganizations_url(String organizations_url) {
                        this.organizations_url = organizations_url;
                    }

                    public String getRepos_url() {
                        return repos_url;
                    }

                    public void setRepos_url(String repos_url) {
                        this.repos_url = repos_url;
                    }

                    public String getEvents_url() {
                        return events_url;
                    }

                    public void setEvents_url(String events_url) {
                        this.events_url = events_url;
                    }

                    public String getReceived_events_url() {
                        return received_events_url;
                    }

                    public void setReceived_events_url(String received_events_url) {
                        this.received_events_url = received_events_url;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public boolean isSite_admin() {
                        return site_admin;
                    }

                    public void setSite_admin(boolean site_admin) {
                        this.site_admin = site_admin;
                    }
                }

                public static class LicenseBean {
                    /**
                     * key : other
                     * name : Other
                     * spdx_id : NOASSERTION
                     * url : null
                     * node_id : MDc6TGljZW5zZTA=
                     */

                    private String key;
                    private String name;
                    private String spdx_id;
                    private Object url;
                    private String node_id;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getSpdx_id() {
                        return spdx_id;
                    }

                    public void setSpdx_id(String spdx_id) {
                        this.spdx_id = spdx_id;
                    }

                    public Object getUrl() {
                        return url;
                    }

                    public void setUrl(Object url) {
                        this.url = url;
                    }

                    public String getNode_id() {
                        return node_id;
                    }

                    public void setNode_id(String node_id) {
                        this.node_id = node_id;
                    }
                }
            }
        }

        public static class BaseBean {
            /**
             * label : golang:master
             * ref : master
             * sha : 6dc740f0928e4c1b43697d8e2d4dbc9804911e79
             * user : {"login":"golang","id":4314092,"node_id":"MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=","avatar_url":"https://avatars3.githubusercontent.com/u/4314092?v=4","gravatar_id":"","url":"https://api.github.com/users/golang","html_url":"https://github.com/golang","followers_url":"https://api.github.com/users/golang/followers","following_url":"https://api.github.com/users/golang/following{/other_user}","gists_url":"https://api.github.com/users/golang/gists{/gist_id}","starred_url":"https://api.github.com/users/golang/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/golang/subscriptions","organizations_url":"https://api.github.com/users/golang/orgs","repos_url":"https://api.github.com/users/golang/repos","events_url":"https://api.github.com/users/golang/events{/privacy}","received_events_url":"https://api.github.com/users/golang/received_events","type":"Organization","site_admin":false}
             * repo : {"id":23096959,"node_id":"MDEwOlJlcG9zaXRvcnkyMzA5Njk1OQ==","name":"go","full_name":"golang/go","private":false,"owner":{"login":"golang","id":4314092,"node_id":"MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=","avatar_url":"https://avatars3.githubusercontent.com/u/4314092?v=4","gravatar_id":"","url":"https://api.github.com/users/golang","html_url":"https://github.com/golang","followers_url":"https://api.github.com/users/golang/followers","following_url":"https://api.github.com/users/golang/following{/other_user}","gists_url":"https://api.github.com/users/golang/gists{/gist_id}","starred_url":"https://api.github.com/users/golang/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/golang/subscriptions","organizations_url":"https://api.github.com/users/golang/orgs","repos_url":"https://api.github.com/users/golang/repos","events_url":"https://api.github.com/users/golang/events{/privacy}","received_events_url":"https://api.github.com/users/golang/received_events","type":"Organization","site_admin":false},"html_url":"https://github.com/golang/go","description":"The Go programming language","fork":false,"url":"https://api.github.com/repos/golang/go","forks_url":"https://api.github.com/repos/golang/go/forks","keys_url":"https://api.github.com/repos/golang/go/keys{/key_id}","collaborators_url":"https://api.github.com/repos/golang/go/collaborators{/collaborator}","teams_url":"https://api.github.com/repos/golang/go/teams","hooks_url":"https://api.github.com/repos/golang/go/hooks","issue_events_url":"https://api.github.com/repos/golang/go/issues/events{/number}","events_url":"https://api.github.com/repos/golang/go/events","assignees_url":"https://api.github.com/repos/golang/go/assignees{/user}","branches_url":"https://api.github.com/repos/golang/go/branches{/branch}","tags_url":"https://api.github.com/repos/golang/go/tags","blobs_url":"https://api.github.com/repos/golang/go/git/blobs{/sha}","git_tags_url":"https://api.github.com/repos/golang/go/git/tags{/sha}","git_refs_url":"https://api.github.com/repos/golang/go/git/refs{/sha}","trees_url":"https://api.github.com/repos/golang/go/git/trees{/sha}","statuses_url":"https://api.github.com/repos/golang/go/statuses/{sha}","languages_url":"https://api.github.com/repos/golang/go/languages","stargazers_url":"https://api.github.com/repos/golang/go/stargazers","contributors_url":"https://api.github.com/repos/golang/go/contributors","subscribers_url":"https://api.github.com/repos/golang/go/subscribers","subscription_url":"https://api.github.com/repos/golang/go/subscription","commits_url":"https://api.github.com/repos/golang/go/commits{/sha}","git_commits_url":"https://api.github.com/repos/golang/go/git/commits{/sha}","comments_url":"https://api.github.com/repos/golang/go/comments{/number}","issue_comment_url":"https://api.github.com/repos/golang/go/issues/comments{/number}","contents_url":"https://api.github.com/repos/golang/go/contents/{+path}","compare_url":"https://api.github.com/repos/golang/go/compare/{base}...{head}","merges_url":"https://api.github.com/repos/golang/go/merges","archive_url":"https://api.github.com/repos/golang/go/{archive_format}{/ref}","downloads_url":"https://api.github.com/repos/golang/go/downloads","issues_url":"https://api.github.com/repos/golang/go/issues{/number}","pulls_url":"https://api.github.com/repos/golang/go/pulls{/number}","milestones_url":"https://api.github.com/repos/golang/go/milestones{/number}","notifications_url":"https://api.github.com/repos/golang/go/notifications{?since,all,participating}","labels_url":"https://api.github.com/repos/golang/go/labels{/name}","releases_url":"https://api.github.com/repos/golang/go/releases{/id}","deployments_url":"https://api.github.com/repos/golang/go/deployments","created_at":"2014-08-19T04:33:40Z","updated_at":"2019-10-11T09:54:55Z","pushed_at":"2019-10-11T04:02:02Z","git_url":"git://github.com/golang/go.git","ssh_url":"git@github.com:golang/go.git","clone_url":"https://github.com/golang/go.git","svn_url":"https://github.com/golang/go","homepage":"https://golang.org","size":194445,"stargazers_count":64398,"watchers_count":64398,"language":"Go","has_issues":true,"has_projects":false,"has_downloads":true,"has_wiki":true,"has_pages":false,"forks_count":9036,"mirror_url":null,"archived":false,"disabled":false,"open_issues_count":5027,"license":{"key":"other","name":"Other","spdx_id":"NOASSERTION","url":null,"node_id":"MDc6TGljZW5zZTA="},"forks":9036,"open_issues":5027,"watchers":64398,"default_branch":"master"}
             */

            private String label;
            private String ref;
            private String sha;
            private UserBeanXX user;
            private RepoBeanX repo;

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public String getRef() {
                return ref;
            }

            public void setRef(String ref) {
                this.ref = ref;
            }

            public String getSha() {
                return sha;
            }

            public void setSha(String sha) {
                this.sha = sha;
            }

            public UserBeanXX getUser() {
                return user;
            }

            public void setUser(UserBeanXX user) {
                this.user = user;
            }

            public RepoBeanX getRepo() {
                return repo;
            }

            public void setRepo(RepoBeanX repo) {
                this.repo = repo;
            }

            public static class UserBeanXX {
                /**
                 * login : golang
                 * id : 4314092
                 * node_id : MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=
                 * avatar_url : https://avatars3.githubusercontent.com/u/4314092?v=4
                 * gravatar_id :
                 * url : https://api.github.com/users/golang
                 * html_url : https://github.com/golang
                 * followers_url : https://api.github.com/users/golang/followers
                 * following_url : https://api.github.com/users/golang/following{/other_user}
                 * gists_url : https://api.github.com/users/golang/gists{/gist_id}
                 * starred_url : https://api.github.com/users/golang/starred{/owner}{/repo}
                 * subscriptions_url : https://api.github.com/users/golang/subscriptions
                 * organizations_url : https://api.github.com/users/golang/orgs
                 * repos_url : https://api.github.com/users/golang/repos
                 * events_url : https://api.github.com/users/golang/events{/privacy}
                 * received_events_url : https://api.github.com/users/golang/received_events
                 * type : Organization
                 * site_admin : false
                 */

                private String login;
                private int id;
                private String node_id;
                private String avatar_url;
                private String gravatar_id;
                private String url;
                private String html_url;
                private String followers_url;
                private String following_url;
                private String gists_url;
                private String starred_url;
                private String subscriptions_url;
                private String organizations_url;
                private String repos_url;
                private String events_url;
                private String received_events_url;
                private String type;
                private boolean site_admin;

                public String getLogin() {
                    return login;
                }

                public void setLogin(String login) {
                    this.login = login;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getNode_id() {
                    return node_id;
                }

                public void setNode_id(String node_id) {
                    this.node_id = node_id;
                }

                public String getAvatar_url() {
                    return avatar_url;
                }

                public void setAvatar_url(String avatar_url) {
                    this.avatar_url = avatar_url;
                }

                public String getGravatar_id() {
                    return gravatar_id;
                }

                public void setGravatar_id(String gravatar_id) {
                    this.gravatar_id = gravatar_id;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getHtml_url() {
                    return html_url;
                }

                public void setHtml_url(String html_url) {
                    this.html_url = html_url;
                }

                public String getFollowers_url() {
                    return followers_url;
                }

                public void setFollowers_url(String followers_url) {
                    this.followers_url = followers_url;
                }

                public String getFollowing_url() {
                    return following_url;
                }

                public void setFollowing_url(String following_url) {
                    this.following_url = following_url;
                }

                public String getGists_url() {
                    return gists_url;
                }

                public void setGists_url(String gists_url) {
                    this.gists_url = gists_url;
                }

                public String getStarred_url() {
                    return starred_url;
                }

                public void setStarred_url(String starred_url) {
                    this.starred_url = starred_url;
                }

                public String getSubscriptions_url() {
                    return subscriptions_url;
                }

                public void setSubscriptions_url(String subscriptions_url) {
                    this.subscriptions_url = subscriptions_url;
                }

                public String getOrganizations_url() {
                    return organizations_url;
                }

                public void setOrganizations_url(String organizations_url) {
                    this.organizations_url = organizations_url;
                }

                public String getRepos_url() {
                    return repos_url;
                }

                public void setRepos_url(String repos_url) {
                    this.repos_url = repos_url;
                }

                public String getEvents_url() {
                    return events_url;
                }

                public void setEvents_url(String events_url) {
                    this.events_url = events_url;
                }

                public String getReceived_events_url() {
                    return received_events_url;
                }

                public void setReceived_events_url(String received_events_url) {
                    this.received_events_url = received_events_url;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public boolean isSite_admin() {
                    return site_admin;
                }

                public void setSite_admin(boolean site_admin) {
                    this.site_admin = site_admin;
                }
            }

            public static class RepoBeanX {
                /**
                 * id : 23096959
                 * node_id : MDEwOlJlcG9zaXRvcnkyMzA5Njk1OQ==
                 * name : go
                 * full_name : golang/go
                 * private : false
                 * owner : {"login":"golang","id":4314092,"node_id":"MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=","avatar_url":"https://avatars3.githubusercontent.com/u/4314092?v=4","gravatar_id":"","url":"https://api.github.com/users/golang","html_url":"https://github.com/golang","followers_url":"https://api.github.com/users/golang/followers","following_url":"https://api.github.com/users/golang/following{/other_user}","gists_url":"https://api.github.com/users/golang/gists{/gist_id}","starred_url":"https://api.github.com/users/golang/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/golang/subscriptions","organizations_url":"https://api.github.com/users/golang/orgs","repos_url":"https://api.github.com/users/golang/repos","events_url":"https://api.github.com/users/golang/events{/privacy}","received_events_url":"https://api.github.com/users/golang/received_events","type":"Organization","site_admin":false}
                 * html_url : https://github.com/golang/go
                 * description : The Go programming language
                 * fork : false
                 * url : https://api.github.com/repos/golang/go
                 * forks_url : https://api.github.com/repos/golang/go/forks
                 * keys_url : https://api.github.com/repos/golang/go/keys{/key_id}
                 * collaborators_url : https://api.github.com/repos/golang/go/collaborators{/collaborator}
                 * teams_url : https://api.github.com/repos/golang/go/teams
                 * hooks_url : https://api.github.com/repos/golang/go/hooks
                 * issue_events_url : https://api.github.com/repos/golang/go/issues/events{/number}
                 * events_url : https://api.github.com/repos/golang/go/events
                 * assignees_url : https://api.github.com/repos/golang/go/assignees{/user}
                 * branches_url : https://api.github.com/repos/golang/go/branches{/branch}
                 * tags_url : https://api.github.com/repos/golang/go/tags
                 * blobs_url : https://api.github.com/repos/golang/go/git/blobs{/sha}
                 * git_tags_url : https://api.github.com/repos/golang/go/git/tags{/sha}
                 * git_refs_url : https://api.github.com/repos/golang/go/git/refs{/sha}
                 * trees_url : https://api.github.com/repos/golang/go/git/trees{/sha}
                 * statuses_url : https://api.github.com/repos/golang/go/statuses/{sha}
                 * languages_url : https://api.github.com/repos/golang/go/languages
                 * stargazers_url : https://api.github.com/repos/golang/go/stargazers
                 * contributors_url : https://api.github.com/repos/golang/go/contributors
                 * subscribers_url : https://api.github.com/repos/golang/go/subscribers
                 * subscription_url : https://api.github.com/repos/golang/go/subscription
                 * commits_url : https://api.github.com/repos/golang/go/commits{/sha}
                 * git_commits_url : https://api.github.com/repos/golang/go/git/commits{/sha}
                 * comments_url : https://api.github.com/repos/golang/go/comments{/number}
                 * issue_comment_url : https://api.github.com/repos/golang/go/issues/comments{/number}
                 * contents_url : https://api.github.com/repos/golang/go/contents/{+path}
                 * compare_url : https://api.github.com/repos/golang/go/compare/{base}...{head}
                 * merges_url : https://api.github.com/repos/golang/go/merges
                 * archive_url : https://api.github.com/repos/golang/go/{archive_format}{/ref}
                 * downloads_url : https://api.github.com/repos/golang/go/downloads
                 * issues_url : https://api.github.com/repos/golang/go/issues{/number}
                 * pulls_url : https://api.github.com/repos/golang/go/pulls{/number}
                 * milestones_url : https://api.github.com/repos/golang/go/milestones{/number}
                 * notifications_url : https://api.github.com/repos/golang/go/notifications{?since,all,participating}
                 * labels_url : https://api.github.com/repos/golang/go/labels{/name}
                 * releases_url : https://api.github.com/repos/golang/go/releases{/id}
                 * deployments_url : https://api.github.com/repos/golang/go/deployments
                 * created_at : 2014-08-19T04:33:40Z
                 * updated_at : 2019-10-11T09:54:55Z
                 * pushed_at : 2019-10-11T04:02:02Z
                 * git_url : git://github.com/golang/go.git
                 * ssh_url : git@github.com:golang/go.git
                 * clone_url : https://github.com/golang/go.git
                 * svn_url : https://github.com/golang/go
                 * homepage : https://golang.org
                 * size : 194445
                 * stargazers_count : 64398
                 * watchers_count : 64398
                 * language : Go
                 * has_issues : true
                 * has_projects : false
                 * has_downloads : true
                 * has_wiki : true
                 * has_pages : false
                 * forks_count : 9036
                 * mirror_url : null
                 * archived : false
                 * disabled : false
                 * open_issues_count : 5027
                 * license : {"key":"other","name":"Other","spdx_id":"NOASSERTION","url":null,"node_id":"MDc6TGljZW5zZTA="}
                 * forks : 9036
                 * open_issues : 5027
                 * watchers : 64398
                 * default_branch : master
                 */

                private int id;
                private String node_id;
                private String name;
                private String full_name;
                @com.google.gson.annotations.SerializedName("private")
                private boolean privateX;
                private OwnerBeanX owner;
                private String html_url;
                private String description;
                private boolean fork;
                private String url;
                private String forks_url;
                private String keys_url;
                private String collaborators_url;
                private String teams_url;
                private String hooks_url;
                private String issue_events_url;
                private String events_url;
                private String assignees_url;
                private String branches_url;
                private String tags_url;
                private String blobs_url;
                private String git_tags_url;
                private String git_refs_url;
                private String trees_url;
                private String statuses_url;
                private String languages_url;
                private String stargazers_url;
                private String contributors_url;
                private String subscribers_url;
                private String subscription_url;
                private String commits_url;
                private String git_commits_url;
                private String comments_url;
                private String issue_comment_url;
                private String contents_url;
                private String compare_url;
                private String merges_url;
                private String archive_url;
                private String downloads_url;
                private String issues_url;
                private String pulls_url;
                private String milestones_url;
                private String notifications_url;
                private String labels_url;
                private String releases_url;
                private String deployments_url;
                private String created_at;
                private String updated_at;
                private String pushed_at;
                private String git_url;
                private String ssh_url;
                private String clone_url;
                private String svn_url;
                private String homepage;
                private int size;
                private int stargazers_count;
                private int watchers_count;
                private String language;
                private boolean has_issues;
                private boolean has_projects;
                private boolean has_downloads;
                private boolean has_wiki;
                private boolean has_pages;
                private int forks_count;
                private Object mirror_url;
                private boolean archived;
                private boolean disabled;
                private int open_issues_count;
                private LicenseBeanX license;
                private int forks;
                private int open_issues;
                private int watchers;
                private String default_branch;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getNode_id() {
                    return node_id;
                }

                public void setNode_id(String node_id) {
                    this.node_id = node_id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getFull_name() {
                    return full_name;
                }

                public void setFull_name(String full_name) {
                    this.full_name = full_name;
                }

                public boolean isPrivateX() {
                    return privateX;
                }

                public void setPrivateX(boolean privateX) {
                    this.privateX = privateX;
                }

                public OwnerBeanX getOwner() {
                    return owner;
                }

                public void setOwner(OwnerBeanX owner) {
                    this.owner = owner;
                }

                public String getHtml_url() {
                    return html_url;
                }

                public void setHtml_url(String html_url) {
                    this.html_url = html_url;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public boolean isFork() {
                    return fork;
                }

                public void setFork(boolean fork) {
                    this.fork = fork;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getForks_url() {
                    return forks_url;
                }

                public void setForks_url(String forks_url) {
                    this.forks_url = forks_url;
                }

                public String getKeys_url() {
                    return keys_url;
                }

                public void setKeys_url(String keys_url) {
                    this.keys_url = keys_url;
                }

                public String getCollaborators_url() {
                    return collaborators_url;
                }

                public void setCollaborators_url(String collaborators_url) {
                    this.collaborators_url = collaborators_url;
                }

                public String getTeams_url() {
                    return teams_url;
                }

                public void setTeams_url(String teams_url) {
                    this.teams_url = teams_url;
                }

                public String getHooks_url() {
                    return hooks_url;
                }

                public void setHooks_url(String hooks_url) {
                    this.hooks_url = hooks_url;
                }

                public String getIssue_events_url() {
                    return issue_events_url;
                }

                public void setIssue_events_url(String issue_events_url) {
                    this.issue_events_url = issue_events_url;
                }

                public String getEvents_url() {
                    return events_url;
                }

                public void setEvents_url(String events_url) {
                    this.events_url = events_url;
                }

                public String getAssignees_url() {
                    return assignees_url;
                }

                public void setAssignees_url(String assignees_url) {
                    this.assignees_url = assignees_url;
                }

                public String getBranches_url() {
                    return branches_url;
                }

                public void setBranches_url(String branches_url) {
                    this.branches_url = branches_url;
                }

                public String getTags_url() {
                    return tags_url;
                }

                public void setTags_url(String tags_url) {
                    this.tags_url = tags_url;
                }

                public String getBlobs_url() {
                    return blobs_url;
                }

                public void setBlobs_url(String blobs_url) {
                    this.blobs_url = blobs_url;
                }

                public String getGit_tags_url() {
                    return git_tags_url;
                }

                public void setGit_tags_url(String git_tags_url) {
                    this.git_tags_url = git_tags_url;
                }

                public String getGit_refs_url() {
                    return git_refs_url;
                }

                public void setGit_refs_url(String git_refs_url) {
                    this.git_refs_url = git_refs_url;
                }

                public String getTrees_url() {
                    return trees_url;
                }

                public void setTrees_url(String trees_url) {
                    this.trees_url = trees_url;
                }

                public String getStatuses_url() {
                    return statuses_url;
                }

                public void setStatuses_url(String statuses_url) {
                    this.statuses_url = statuses_url;
                }

                public String getLanguages_url() {
                    return languages_url;
                }

                public void setLanguages_url(String languages_url) {
                    this.languages_url = languages_url;
                }

                public String getStargazers_url() {
                    return stargazers_url;
                }

                public void setStargazers_url(String stargazers_url) {
                    this.stargazers_url = stargazers_url;
                }

                public String getContributors_url() {
                    return contributors_url;
                }

                public void setContributors_url(String contributors_url) {
                    this.contributors_url = contributors_url;
                }

                public String getSubscribers_url() {
                    return subscribers_url;
                }

                public void setSubscribers_url(String subscribers_url) {
                    this.subscribers_url = subscribers_url;
                }

                public String getSubscription_url() {
                    return subscription_url;
                }

                public void setSubscription_url(String subscription_url) {
                    this.subscription_url = subscription_url;
                }

                public String getCommits_url() {
                    return commits_url;
                }

                public void setCommits_url(String commits_url) {
                    this.commits_url = commits_url;
                }

                public String getGit_commits_url() {
                    return git_commits_url;
                }

                public void setGit_commits_url(String git_commits_url) {
                    this.git_commits_url = git_commits_url;
                }

                public String getComments_url() {
                    return comments_url;
                }

                public void setComments_url(String comments_url) {
                    this.comments_url = comments_url;
                }

                public String getIssue_comment_url() {
                    return issue_comment_url;
                }

                public void setIssue_comment_url(String issue_comment_url) {
                    this.issue_comment_url = issue_comment_url;
                }

                public String getContents_url() {
                    return contents_url;
                }

                public void setContents_url(String contents_url) {
                    this.contents_url = contents_url;
                }

                public String getCompare_url() {
                    return compare_url;
                }

                public void setCompare_url(String compare_url) {
                    this.compare_url = compare_url;
                }

                public String getMerges_url() {
                    return merges_url;
                }

                public void setMerges_url(String merges_url) {
                    this.merges_url = merges_url;
                }

                public String getArchive_url() {
                    return archive_url;
                }

                public void setArchive_url(String archive_url) {
                    this.archive_url = archive_url;
                }

                public String getDownloads_url() {
                    return downloads_url;
                }

                public void setDownloads_url(String downloads_url) {
                    this.downloads_url = downloads_url;
                }

                public String getIssues_url() {
                    return issues_url;
                }

                public void setIssues_url(String issues_url) {
                    this.issues_url = issues_url;
                }

                public String getPulls_url() {
                    return pulls_url;
                }

                public void setPulls_url(String pulls_url) {
                    this.pulls_url = pulls_url;
                }

                public String getMilestones_url() {
                    return milestones_url;
                }

                public void setMilestones_url(String milestones_url) {
                    this.milestones_url = milestones_url;
                }

                public String getNotifications_url() {
                    return notifications_url;
                }

                public void setNotifications_url(String notifications_url) {
                    this.notifications_url = notifications_url;
                }

                public String getLabels_url() {
                    return labels_url;
                }

                public void setLabels_url(String labels_url) {
                    this.labels_url = labels_url;
                }

                public String getReleases_url() {
                    return releases_url;
                }

                public void setReleases_url(String releases_url) {
                    this.releases_url = releases_url;
                }

                public String getDeployments_url() {
                    return deployments_url;
                }

                public void setDeployments_url(String deployments_url) {
                    this.deployments_url = deployments_url;
                }

                public String getCreated_at() {
                    return created_at;
                }

                public void setCreated_at(String created_at) {
                    this.created_at = created_at;
                }

                public String getUpdated_at() {
                    return updated_at;
                }

                public void setUpdated_at(String updated_at) {
                    this.updated_at = updated_at;
                }

                public String getPushed_at() {
                    return pushed_at;
                }

                public void setPushed_at(String pushed_at) {
                    this.pushed_at = pushed_at;
                }

                public String getGit_url() {
                    return git_url;
                }

                public void setGit_url(String git_url) {
                    this.git_url = git_url;
                }

                public String getSsh_url() {
                    return ssh_url;
                }

                public void setSsh_url(String ssh_url) {
                    this.ssh_url = ssh_url;
                }

                public String getClone_url() {
                    return clone_url;
                }

                public void setClone_url(String clone_url) {
                    this.clone_url = clone_url;
                }

                public String getSvn_url() {
                    return svn_url;
                }

                public void setSvn_url(String svn_url) {
                    this.svn_url = svn_url;
                }

                public String getHomepage() {
                    return homepage;
                }

                public void setHomepage(String homepage) {
                    this.homepage = homepage;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public int getStargazers_count() {
                    return stargazers_count;
                }

                public void setStargazers_count(int stargazers_count) {
                    this.stargazers_count = stargazers_count;
                }

                public int getWatchers_count() {
                    return watchers_count;
                }

                public void setWatchers_count(int watchers_count) {
                    this.watchers_count = watchers_count;
                }

                public String getLanguage() {
                    return language;
                }

                public void setLanguage(String language) {
                    this.language = language;
                }

                public boolean isHas_issues() {
                    return has_issues;
                }

                public void setHas_issues(boolean has_issues) {
                    this.has_issues = has_issues;
                }

                public boolean isHas_projects() {
                    return has_projects;
                }

                public void setHas_projects(boolean has_projects) {
                    this.has_projects = has_projects;
                }

                public boolean isHas_downloads() {
                    return has_downloads;
                }

                public void setHas_downloads(boolean has_downloads) {
                    this.has_downloads = has_downloads;
                }

                public boolean isHas_wiki() {
                    return has_wiki;
                }

                public void setHas_wiki(boolean has_wiki) {
                    this.has_wiki = has_wiki;
                }

                public boolean isHas_pages() {
                    return has_pages;
                }

                public void setHas_pages(boolean has_pages) {
                    this.has_pages = has_pages;
                }

                public int getForks_count() {
                    return forks_count;
                }

                public void setForks_count(int forks_count) {
                    this.forks_count = forks_count;
                }

                public Object getMirror_url() {
                    return mirror_url;
                }

                public void setMirror_url(Object mirror_url) {
                    this.mirror_url = mirror_url;
                }

                public boolean isArchived() {
                    return archived;
                }

                public void setArchived(boolean archived) {
                    this.archived = archived;
                }

                public boolean isDisabled() {
                    return disabled;
                }

                public void setDisabled(boolean disabled) {
                    this.disabled = disabled;
                }

                public int getOpen_issues_count() {
                    return open_issues_count;
                }

                public void setOpen_issues_count(int open_issues_count) {
                    this.open_issues_count = open_issues_count;
                }

                public LicenseBeanX getLicense() {
                    return license;
                }

                public void setLicense(LicenseBeanX license) {
                    this.license = license;
                }

                public int getForks() {
                    return forks;
                }

                public void setForks(int forks) {
                    this.forks = forks;
                }

                public int getOpen_issues() {
                    return open_issues;
                }

                public void setOpen_issues(int open_issues) {
                    this.open_issues = open_issues;
                }

                public int getWatchers() {
                    return watchers;
                }

                public void setWatchers(int watchers) {
                    this.watchers = watchers;
                }

                public String getDefault_branch() {
                    return default_branch;
                }

                public void setDefault_branch(String default_branch) {
                    this.default_branch = default_branch;
                }

                public static class OwnerBeanX {
                    /**
                     * login : golang
                     * id : 4314092
                     * node_id : MDEyOk9yZ2FuaXphdGlvbjQzMTQwOTI=
                     * avatar_url : https://avatars3.githubusercontent.com/u/4314092?v=4
                     * gravatar_id :
                     * url : https://api.github.com/users/golang
                     * html_url : https://github.com/golang
                     * followers_url : https://api.github.com/users/golang/followers
                     * following_url : https://api.github.com/users/golang/following{/other_user}
                     * gists_url : https://api.github.com/users/golang/gists{/gist_id}
                     * starred_url : https://api.github.com/users/golang/starred{/owner}{/repo}
                     * subscriptions_url : https://api.github.com/users/golang/subscriptions
                     * organizations_url : https://api.github.com/users/golang/orgs
                     * repos_url : https://api.github.com/users/golang/repos
                     * events_url : https://api.github.com/users/golang/events{/privacy}
                     * received_events_url : https://api.github.com/users/golang/received_events
                     * type : Organization
                     * site_admin : false
                     */

                    private String login;
                    private int id;
                    private String node_id;
                    private String avatar_url;
                    private String gravatar_id;
                    private String url;
                    private String html_url;
                    private String followers_url;
                    private String following_url;
                    private String gists_url;
                    private String starred_url;
                    private String subscriptions_url;
                    private String organizations_url;
                    private String repos_url;
                    private String events_url;
                    private String received_events_url;
                    private String type;
                    private boolean site_admin;

                    public String getLogin() {
                        return login;
                    }

                    public void setLogin(String login) {
                        this.login = login;
                    }

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public String getNode_id() {
                        return node_id;
                    }

                    public void setNode_id(String node_id) {
                        this.node_id = node_id;
                    }

                    public String getAvatar_url() {
                        return avatar_url;
                    }

                    public void setAvatar_url(String avatar_url) {
                        this.avatar_url = avatar_url;
                    }

                    public String getGravatar_id() {
                        return gravatar_id;
                    }

                    public void setGravatar_id(String gravatar_id) {
                        this.gravatar_id = gravatar_id;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getHtml_url() {
                        return html_url;
                    }

                    public void setHtml_url(String html_url) {
                        this.html_url = html_url;
                    }

                    public String getFollowers_url() {
                        return followers_url;
                    }

                    public void setFollowers_url(String followers_url) {
                        this.followers_url = followers_url;
                    }

                    public String getFollowing_url() {
                        return following_url;
                    }

                    public void setFollowing_url(String following_url) {
                        this.following_url = following_url;
                    }

                    public String getGists_url() {
                        return gists_url;
                    }

                    public void setGists_url(String gists_url) {
                        this.gists_url = gists_url;
                    }

                    public String getStarred_url() {
                        return starred_url;
                    }

                    public void setStarred_url(String starred_url) {
                        this.starred_url = starred_url;
                    }

                    public String getSubscriptions_url() {
                        return subscriptions_url;
                    }

                    public void setSubscriptions_url(String subscriptions_url) {
                        this.subscriptions_url = subscriptions_url;
                    }

                    public String getOrganizations_url() {
                        return organizations_url;
                    }

                    public void setOrganizations_url(String organizations_url) {
                        this.organizations_url = organizations_url;
                    }

                    public String getRepos_url() {
                        return repos_url;
                    }

                    public void setRepos_url(String repos_url) {
                        this.repos_url = repos_url;
                    }

                    public String getEvents_url() {
                        return events_url;
                    }

                    public void setEvents_url(String events_url) {
                        this.events_url = events_url;
                    }

                    public String getReceived_events_url() {
                        return received_events_url;
                    }

                    public void setReceived_events_url(String received_events_url) {
                        this.received_events_url = received_events_url;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public boolean isSite_admin() {
                        return site_admin;
                    }

                    public void setSite_admin(boolean site_admin) {
                        this.site_admin = site_admin;
                    }
                }

                public static class LicenseBeanX {
                    /**
                     * key : other
                     * name : Other
                     * spdx_id : NOASSERTION
                     * url : null
                     * node_id : MDc6TGljZW5zZTA=
                     */

                    private String key;
                    private String name;
                    private String spdx_id;
                    private Object url;
                    private String node_id;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public String getSpdx_id() {
                        return spdx_id;
                    }

                    public void setSpdx_id(String spdx_id) {
                        this.spdx_id = spdx_id;
                    }

                    public Object getUrl() {
                        return url;
                    }

                    public void setUrl(Object url) {
                        this.url = url;
                    }

                    public String getNode_id() {
                        return node_id;
                    }

                    public void setNode_id(String node_id) {
                        this.node_id = node_id;
                    }
                }
            }
        }

        public static class LinksBean {
            /**
             * self : {"href":"https://api.github.com/repos/golang/go/pulls/34832"}
             * html : {"href":"https://github.com/golang/go/pull/34832"}
             * issue : {"href":"https://api.github.com/repos/golang/go/issues/34832"}
             * comments : {"href":"https://api.github.com/repos/golang/go/issues/34832/comments"}
             * review_comments : {"href":"https://api.github.com/repos/golang/go/pulls/34832/comments"}
             * review_comment : {"href":"https://api.github.com/repos/golang/go/pulls/comments{/number}"}
             * commits : {"href":"https://api.github.com/repos/golang/go/pulls/34832/commits"}
             * statuses : {"href":"https://api.github.com/repos/golang/go/statuses/d132293b705cc89eaffc3d780c687809b67b665e"}
             */

            private SelfBean self;
            private HtmlBean html;
            private IssueBean issue;
            private CommentsBean comments;
            private ReviewCommentsBean review_comments;
            private ReviewCommentBean review_comment;
            private CommitsBean commits;
            private StatusesBean statuses;

            public SelfBean getSelf() {
                return self;
            }

            public void setSelf(SelfBean self) {
                this.self = self;
            }

            public HtmlBean getHtml() {
                return html;
            }

            public void setHtml(HtmlBean html) {
                this.html = html;
            }

            public IssueBean getIssue() {
                return issue;
            }

            public void setIssue(IssueBean issue) {
                this.issue = issue;
            }

            public CommentsBean getComments() {
                return comments;
            }

            public void setComments(CommentsBean comments) {
                this.comments = comments;
            }

            public ReviewCommentsBean getReview_comments() {
                return review_comments;
            }

            public void setReview_comments(ReviewCommentsBean review_comments) {
                this.review_comments = review_comments;
            }

            public ReviewCommentBean getReview_comment() {
                return review_comment;
            }

            public void setReview_comment(ReviewCommentBean review_comment) {
                this.review_comment = review_comment;
            }

            public CommitsBean getCommits() {
                return commits;
            }

            public void setCommits(CommitsBean commits) {
                this.commits = commits;
            }

            public StatusesBean getStatuses() {
                return statuses;
            }

            public void setStatuses(StatusesBean statuses) {
                this.statuses = statuses;
            }

            public static class SelfBean {
                /**
                 * href : https://api.github.com/repos/golang/go/pulls/34832
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class HtmlBean {
                /**
                 * href : https://github.com/golang/go/pull/34832
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class IssueBean {
                /**
                 * href : https://api.github.com/repos/golang/go/issues/34832
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class CommentsBean {
                /**
                 * href : https://api.github.com/repos/golang/go/issues/34832/comments
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class ReviewCommentsBean {
                /**
                 * href : https://api.github.com/repos/golang/go/pulls/34832/comments
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class ReviewCommentBean {
                /**
                 * href : https://api.github.com/repos/golang/go/pulls/comments{/number}
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class CommitsBean {
                /**
                 * href : https://api.github.com/repos/golang/go/pulls/34832/commits
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }

            public static class StatusesBean {
                /**
                 * href : https://api.github.com/repos/golang/go/statuses/d132293b705cc89eaffc3d780c687809b67b665e
                 */

                private String href;

                public String getHref() {
                    return href;
                }

                public void setHref(String href) {
                    this.href = href;
                }
            }
        }

        public static class LabelsBean {
            /**
             * id : 831707000
             * node_id : MDU6TGFiZWw4MzE3MDcwMDA=
             * url : https://api.github.com/repos/golang/go/labels/cla:%20yes
             * name : cla: yes
             * color : 0e8a16
             * default : false
             */

            private int id;
            private String node_id;
            private String url;
            private String name;
            private String color;
            @com.google.gson.annotations.SerializedName("default")
            private boolean defaultX;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNode_id() {
                return node_id;
            }

            public void setNode_id(String node_id) {
                this.node_id = node_id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public boolean isDefaultX() {
                return defaultX;
            }

            public void setDefaultX(boolean defaultX) {
                this.defaultX = defaultX;
            }
        }
    }


}
