layui.use(['form', 'table', 'laydate'], function () {
    var $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        laydate = layui.laydate;

    //日期范围
    laydate.render({
        elem: '#date-range',
        range: '~',
        theme: 'molv'
    });

    table.render({
        elem: '#currentTableId',
        url: '/admin/user',
        toolbar: '#toolbarDemo',
        defaultToolbar: ['filter', 'exports', 'print', {
            title: '提示',
            layEvent: 'LAYTABLE_TIPS',
            icon: 'layui-icon-tips'
        }],
        cols: [
            [
                {type: "checkbox", width: 50, fixed: "left"},
                {field: 'id', width: 80, title: 'ID', sort: true},
                {field: 'username', width: 150, title: '用户名', sort: true},
                {field: 'nickname', width: 150, title: '昵称'},
                {
                    field: 'role', title: '角色', width: 100, sort: true, align: 'center', templet: function (data) {
                        if (data.role !== undefined) {
                            let roleName = data.role.roleName;
                            let color = 'layui-bg-' + data.role.color;
                            return '<span class="layui-badge ' + color + '">' + roleName + '</span>';
                        } else {
                            return '-';
                        }
                    }
                },
                {
                    field: 'status',
                    width: 150,
                    title: '状态',
                    sort: true,
                    align: 'center',
                    hide: (!hasPermission('sys:user:edit')),
                    templet: function (data) {
                        let isChecked = data.status == 1 ? "checked" : "";
                        return '<input type="checkbox" lay-skin="switch" lay-text="启用|禁用" lay-filter="status-filter"' + isChecked + '> ';
                    }
                },
                {field: 'email', width: 150, title: '邮箱'},
                {field: 'createTime', title: '创建时间', minWidth: 200, sort: true},
                {field: 'updateTime', title: '更新时间', minWidth: 200, sort: true},
                {
                    title: '操作',
                    minWidth: 150,
                    toolbar: '#currentTableBar',
                    fixed: "right",
                    align: "center",
                    hide: (!hasPermission('sys:user:edit') && !hasPermission('sys:user:delete'))
                }
            ]
        ],
        limits: [10, 15, 20, 25, 50, 100],
        limit: 15,
        page: true,
        done: function () {
            checkPermission();
        }
    });

    // 监听搜索操作
    form.on('submit(data-search-btn)', function (data) {
        let username = data.field.username;
        let email = data.field.email;
        let dateRange = data.field.dateRange;
        let dates = dateRange.split(' ~ ')
        let startDate = dates[0];
        let endDate = dates[1];

        //执行搜索重载
        table.reload('currentTableId', {
            page: {
                curr: 1
            }
            , where: {
                username: username,
                email: email,
                startDate: startDate,
                endDate: endDate
            }
        }, 'data');

        return false;
    });

    //监听状态开关
    form.on('switch(status-filter)', function (obj) {
        let status = obj.elem.checked ? 1 : 0;
        let userId = obj.othis.parents('tr').find("td").eq(1).text();
        let username = obj.othis.parents('tr').find("td").eq(2).text()
        let op = (status === 1 ? '启用' : '停用');
        let msg = '是否' + op + '用户:' + username + '?';
        let x = obj.elem.checked;
        Swal.fire({
            title: msg,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '确定！',
            cancelButtonText: '取消！',
            showLoaderOnConfirm: true,
            preConfirm: () => {
                return new Promise((resolve, reject) => {
                    axios({
                        method: 'put',
                        url: '/admin/user/status',
                        data: {
                            id: userId,
                            status: status,
                        },
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest'
                        }
                    })
                        .then(response => {
                            resolve(response.data);
                        }, error => {
                            reject(error);
                        })
                }).then(res => {
                    Swal.fire({
                        icon: 'success',
                        title: '修改成功！',
                        showConfirmButton: false,
                        timer: 1500
                    });
                }).catch(error => {
                    Swal.showValidationMessage(`修改失败: ${error}`);
                })
            }
        }).then((result) => {
            if (result.dismiss === Swal.DismissReason.cancel) {
                obj.elem.checked = !x;
            }
            form.render();
        })
        return false;
    });

    /**
     * toolbar监听事件
     */
    table.on('toolbar(currentTableFilter)', function (obj) {
        if (obj.event === 'add') {  // 监听添加操作
            var index = layer.open({
                title: '添加用户',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '80%'],
                content: '/admin/page/user/user-add',
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
        } else if (obj.event === 'delete') {  // 监听删除操作
            var checkStatus = table.checkStatus('currentTableId')
                , data = checkStatus.data;
            var idList = [];
            $.each(data, function () {
                idList.push($(this)[0].id);
            });
            if (idList.length <= 0) {
                return false;
            }
            Swal.fire({
                title: '确定删除选中的' + idList.length + '个用户吗？',
                text: '你将无法恢复它！',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '确定删除！',
                cancelButtonText: '取消删除！',
                showLoaderOnConfirm: true,
                preConfirm: () => {
                    return new Promise((resolve, reject) => {
                        axios({
                            method: 'delete',
                            url: '/admin/user',
                            data: idList,
                            headers: {
                                'X-Requested-With': 'XMLHttpRequest'
                            }
                        })
                            .then(response => {
                                table.reload('currentTableId');
                                resolve(response.data);
                            }, error => {
                                reject(error);
                            })
                    }).then(res => {
                        Swal.fire({
                            icon: 'success',
                            title: '删除成功！',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    }).catch(error => {
                        Swal.showValidationMessage(`删除失败: ${error.response.data.message}`);
                    })
                }
            });
        }
    });

    table.on('tool(currentTableFilter)', function (obj) {
        var data = obj.data;
        if (obj.event === 'edit') {

            var index = layer.open({
                title: '编辑用户',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '80%'],
                content: '/admin/user/' + data.id,
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
            return false;
        } else if (obj.event === 'delete') {
            Swal.fire({
                title: '确定删除选中的这个用户吗？',
                text: '你将无法恢复它！',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '确定删除！',
                cancelButtonText: '取消删除！',
                showLoaderOnConfirm: true,
                preConfirm: () => {
                    return new Promise((resolve, reject) => {
                        axios({
                            method: 'delete',
                            url: '/admin/user/' + data.id,
                            headers: {
                                'X-Requested-With': 'XMLHttpRequest'
                            }
                        })
                            .then(response => {
                                obj.del();
                                resolve(response.data);
                            }, error => {
                                reject(error);
                            })
                    }).then(res => {
                        Swal.fire({
                            icon: 'success',
                            title: '删除成功！',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    }).catch(error => {
                        Swal.showValidationMessage(`删除失败: ${error.response.data.message}`);
                    })
                }
            });
        }
    });

});