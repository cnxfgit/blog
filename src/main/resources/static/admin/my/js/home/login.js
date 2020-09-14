layui.use(['form'], function () {
    var form = layui.form,
        layer = layui.layer;

    //刷新验证码
    $('#verCodeImg').click(function flushVerCode() {
        this.src = this.src + "?" + Math.random();
    });

    // 登录过期的时候，跳出iframe框架
    if (top.location != self.location) top.location = self.location;

    // 粒子线条背景
    $(document).ready(function () {
        $('.layui-container').particleground({
            dotColor: '#5cbdaa',
            lineColor: '#5cbdaa'
        });
    });

    // 进行登录操作
    form.on('submit(login)', function (data) {
        data = data.field;
        if (data.username === '') {
            layer.msg('用户名不能为空');
            return false;
        }
        if (data.password === '') {
            layer.msg('密码不能为空');
            return false;
        }
        axios({
            method: 'post',
            url: '/admin/login',
            data: $("#login-form").serialize(),
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        }).then((response) => {
            let result = response.data;
            console.log(result)
            if (result.authenticated) {
                localStorage.setItem("userId", result.principal.id);
                localStorage.setItem("username", result.principal.username);
                localStorage.setItem("nickname", result.principal.nickname);
                localStorage.setItem("user", JSON.stringify(result.principal));
                var authorities = result.authorities;
                var permission = [];
                for (var i in authorities) {
                    permission.push(authorities[i].authority);
                }
                localStorage.setItem("permission", JSON.stringify(permission));
                Swal.fire({
                    toast: true,
                    position: 'center',
                    icon: 'success',
                    title: '登录成功!',
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true,
                    onClose: () => {
                        location.href = '/admin'
                    }
                });
            } else {
                let title = '';
                if (result.message !== undefined) {
                    title = result.message;
                } else {
                    title = '登录异常，请联系管理员';
                }
                let verCodeImg = $('#verCodeImg');
                let src = verCodeImg.prop('src');
                verCodeImg.prop('src', src + "?" + Math.random());
                Swal.fire({
                    toast: true,
                    position: 'center',
                    icon: 'error',
                    title: title,
                    showConfirmButton: false,
                    timer: 1000,
                    timerProgressBar: true,
                });
            }
        }).catch((error) => {
            let verCodeImg = $('#verCodeImg');
            let src = verCodeImg.prop('src');
            verCodeImg.prop('src', src + "?" + Math.random());
            Swal.fire({
                toast: true,
                position: 'center',
                icon: 'error',
                title: `${error.response.data.message}`,
                showConfirmButton: false,
                timer: 2000,
                timerProgressBar: true,
            });
        })
        return false;
    });
});