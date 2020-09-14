layui.use(['form'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.$;

    $('.layui-form input[name="id"]').val(user.id);
    $('.layui-form input[name="username"]').val(user.username);
    $('.layui-form input[name="nickname"]').val(user.nickname);
    $('.layui-form input[name="email"]').val(user.email);
    $('.layui-form input[name="status"]').prop('checked', (user.status === 1));
    form.render();

    //获取角色列表
    $.ajax({
        url: '/admin/role/list',
        type: 'GET',
        success: function (result) {
            if (result.code === 200) {
                var rid = user.roleId;
                $.each(result.data, function () {
                    var roleId = $(this)[0].id;
                    var roleName = $(this)[0].roleName;
                    var roleOption = $('<option></option>').val(roleId).text(roleName);
                    if (rid != null && rid === roleId) {
                        roleOption.prop('selected', true);
                    }
                    $('#role-select').append(roleOption);
                });
                form.render('select');
            }
        }
    });

    //自定义验证规则
    form.verify({
        //验证用户名
        username: function (value) { //value：表单的值、item：表单的DOM对象
            if (value.length < 3 || value.length > 16) {
                return '用户名长度为3-16个字符'
            }
            if (!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)) {
                return '用户名不能有特殊字符';
            }
            if (/(^\_)|(\__)|(\_+$)/.test(value)) {
                return '用户名首尾不能出现下划线\'_\'';
            }
            if (/^\d+\d+\d$/.test(value)) {
                return '用户名不能全为数字';
            }
        },
        //验证昵称
        nickname: function (value) { //value：表单的值、item：表单的DOM对象
            if (value.length < 3 || value.length > 16) {
                return '昵称长度为3-16个字符'
            }
            if (!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)) {
                return '昵称不能有特殊字符';
            }
            if (/(^\_)|(\__)|(\_+$)/.test(value)) {
                return '昵称首尾不能出现下划线\'_\'';
            }
            if (/^\d+\d+\d$/.test(value)) {
                return '昵称不能全为数字';
            }
        },
        //验证角色
        role: function (value) {
            if (null == value || 0 === value.length) {
                return '请为用户选择角色';
            }
        }
    });

    //监听提交
    form.on('submit(saveBtn)', function (data) {
        axios({
            method: 'put',
            url: '/admin/user',
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