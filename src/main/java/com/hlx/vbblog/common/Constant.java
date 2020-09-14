package com.hlx.vbblog.common;


public interface Constant {

    String HOME_TITLE = "首页";

    String HOME_HREF = "/admin/page/home/dashboard";

    String LOGO_TITLE = "VB Blog";

    String LOGO_IMAGE = "/static/admin/layuimini/images/logo.png";

    Integer MAX_TOP_ARTICLES = 6;

    Integer MAX_Update_ARTICLES = 4;

    String PAGE_SIZE = "9";  // 一页文章的最大数量

    String MESSAGES_PAGE_SIZE = "10";  // 一页留言的数量

    String COMMENT_PAGE_SIZE = "10";  // 一页评论的数量

    Integer FILTER_BY_DAY = 1;

    Integer FILTER_BY_MONTH = 2;

    Integer FILTER_BY_YEAR = 3;

    /**
     * 用于IP定位转换
     */
    String REGION = "内网IP|内网IP";

    /**
     * 默认颜色
     */
    String DEFAULT_COLOR = "#D5F5E3";

    String ADMIN_LINK = "http://vegetablebird.top";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * 留言默认头像
     */
    String DEFAULT_AVATAR = "http://vegetablebird.top:888/blog/2020/9/1.png";

    /**
     * 菜单树根结点
     */
    Long MENU_TREE_ROOT = 0L;

    /**
     * 菜单树根结点名称
     */
    String MENU_TREE_ROOT_NAME = "根目录";

    /**
     * 菜单树开始结点
     */
    Long MENU_TREE_START = -1L;

    /**
     * 菜单树复选框未选中
     */
    String MENU_TREE_NOT_SELECTED = "0";

    /**
     * 菜单树复选框选中
     */
    Integer MENU_TREE_SELECTED = 1;

    /**
     * 评论链表根节点
     */
    Long COMMENT_LINKED_LIST_ROOT = 0L;

    /**
     * 审核状态
     */
    Integer AUDIT_PASS = 2;  // 通过

    Integer AUDIT_WAIT = 1;  // 等待审核

    Integer AUDIT_NOT_PASS = 0;  // 未通过

    /**
     * 用户状态
     */
    Integer USER_ENABLE = 1;

    Integer USER_DISABLE = 0;

    /**
     * 显示
     */
    Integer DISPLAY = 1;

    /**
     * 隐藏
     */
    Integer HIDDEN = 0;


    Integer SUCCESS = 1;

    Integer FAILURE = 0;

    Integer NEWEST_PAGE_SIZE = 10;  // 后台展示文章数量

    String USER = "user";

    /**
     * 搜索高亮前标签
     */
    String HIGH_LIGHT_PRE_TAGS = "<em class='search-keyword'>";

    String HIGH_LIGHT_POST_TAGS = "</em>";
}
