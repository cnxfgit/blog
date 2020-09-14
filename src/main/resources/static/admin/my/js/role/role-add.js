layui.use(['form', 'dtree'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        dtree = layui.dtree;

    //渲染多选下拉框
    var colorSelect = xmSelect.render({
        el: '#color-select',
        language: 'zn',
        radio: true,
        clickClose: true,
        model: {
            label: {
                type: 'text',
                text: {
                    template: function (item, sels) {
                        return '<span>' + item.name + '</span>';
                    },
                },
            }
        },
        template({name, value}) {
            let color = 'layui-bg-' + value;
            return name + '<span class="' + color + '" style="position: absolute; right: 10px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>'
        },
        data: [
            {name: '赤', value: 'red'},
            {name: '橙', value: 'orange'},
            {name: '绿', value: 'green'},
            {name: '青', value: 'cyan'},
            {name: '蓝', value: 'blue'},
            {name: '黑', value: 'black'},
            {name: '灰', value: 'gray'},
        ]
    });

    //获取菜单树
    let data = null;
    $.ajax({
        url: '/admin/menu/checkbox-tree',
        type: 'GET',
        async: false,
        success: function (result) {
            data = result.data;
        }
    });

    let DTree = dtree.render({
        elem: "#menuTree",
        initLevel: 2,
        load: true,
        line: true,
        checkbar: true,
        data: data,
    });

    //自定义验证规则
    form.verify({
        //验证用户名
        roleName: function (value) {
            if (value.length < 2 || value.length > 16) {
                return '角色名称长度为2-16个字符'
            }
            if (!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)) {
                return '角色名称不能有特殊字符';
            }
            if (/^\d+\d+\d$/.test(value)) {
                return '角色名称不能全为数字';
            }
        },
        //验证描述
        description: function (value) {
            if (value.length > 100) {
                return '描述长度在100个字符以内'
            }
        },
        //验证颜色
        colorVer: function () {
            if (colorSelect.getValue('value').length <= 0) {
                return '请选择颜色';
            }
        },
        //验证级别
        rank: function (value) {
            if (value < 1 || value > 1024) {
                return '级别数值在1-1024之间'
            }
        },
        //验证权限
        menuTree: function () {
            let checkData = dtree.getCheckbarNodesParam("menuTree");
            if (checkData.length <= 0) {
                return '请选择权限'
            }
        }
    });

    //获取选中ID列表
    function getCheckIdList(checkData) {
        if (checkData == null || checkData.length <= 0) {
            return [];
        }
        let result = [];
        for (let i = 0; i < checkData.length; i++) {
            if (checkData[i].nodeId !== "0") {
                result.push(Number(checkData[i].nodeId));
            }
        }
        return result;
    }

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        //获取颜色
        data.field.color = colorSelect.getValue('value')[0];
        //获得选中的菜单ID列表
        let checkData = dtree.getCheckbarNodesParam("menuTree");
        data.field.menuIdList = getCheckIdList(checkData);
        axios({
            method: 'post',
            url: '/admin/role',
            data: data.field,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
        }).then((response) => {
            Swal.fire({
                icon: 'success',
                title: '保存成功',
                showConfirmButton: false,
                timer: 1500,
                onClose: () => {
                    var parentLayer = parent.layer;
                    var iframeIndex = parentLayer.getFrameIndex(window.name);
                    parent.location.reload();
                    parentLayer.close(iframeIndex);
                }
            });
        }).catch((error) => {
            Swal.fire({
                icon: 'error',
                title: '保存失败！',
                text: `${error.response.data.message}`,
            });
        })
        return false;
    });
});