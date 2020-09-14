layui.use(['iconPickerFa', 'form', 'layer'], function () {
    var iconPickerFa = layui.iconPickerFa,
        form = layui.form,
        layer = layui.layer,
        $ = layui.$;
    //渲染图标选择器
    let icon = null;
    iconPickerFa.render({
        // 选择器，推荐使用input
        elem: '#iconPicker',
        // fa 图标接口
        url: "/static/admin/layuimini/lib/font-awesome-4.7.0/less/variables.less",
        // 是否开启搜索：true/false，默认true
        search: true,
        // 是否开启分页：true/false，默认true
        page: true,
        // 每页显示数量，默认12
        limit: 12,
        // 点击回调
        click: function (data) {
            icon = "fa " + data.icon;
        },
    });

    //获取菜单树
    let data = null;
    $.ajax({
        url: '/admin/menu/radio-tree',
        type: 'GET',
        async: false,
        success: function (result) {
            data = result.data;
        }
    });

    //渲染菜单树
    var tree = xmSelect.render({
        el: '#menuTree',
        model: {label: {type: 'text'}},
        radio: true,
        clickClose: true,
        tree: {
            show: true,
            strict: false,
        },
        height: 'auto',
        data() {
            return data;
        }
    })

    //自定义验证规则
    form.verify({
        //验证菜单标题
        title: function (value) {
            if (value.length < 2 || value.length > 16) {
                return '菜单标题长度为2-16个字符'
            }
            if (!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)) {
                return '菜单标题不能有特殊字符';
            }
            if (/^\d+\d+\d$/.test(value)) {
                return '菜单标题不能全为数字';
            }
        },
        //验证排序值
        sort: function (value) {
            if (value < 1 || value > 1024) {
                return '排序值在1-1024之间'
            }
        },
        pid: function () {
            let value = tree.getValue('value')[0];
            if (value == null) {
                return '请选择上级菜单'
            }
        }
    });

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        data.field.pid = tree.getValue('value')[0];
        data.field.icon = icon;
        axios({
            method: 'post',
            url: '/admin/menu',
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