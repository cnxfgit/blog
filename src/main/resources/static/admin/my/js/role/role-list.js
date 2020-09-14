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
        url: '/admin/role',
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
                {field: 'roleName', width: 150, title: '角色名', sort: true},
                {field: 'description', width: 150, title: '描述'},
                {
                    field: 'rank', width: 100, title: '级别', sort: true, align: 'center', templet: function (data) {
                        let rank = data.rank;
                        return '<span class="layui-badge layui-bg-green">' + rank + '</span>';
                    }
                },
                {
                    field: 'userCount',
                    width: 150,
                    title: '关联用户数量',
                    sort: true,
                    align: 'center',
                    templet: function (data) {
                        return '<span class="layui-badge layui-bg-green">' + data.userCount + '</span>';
                    }
                },
                {
                    field: 'color', width: 100, title: '颜色', align: 'center', templet: function (data) {
                        let color = 'layui-bg-' + data.color;
                        return '<span class="layui-badge ' + color + '">&nbsp;&nbsp;</span>';
                    }
                },
                {field: 'createTime', title: '创建时间', minWidth: 200, sort: true},
                {field: 'updateTime', title: '更新时间', minWidth: 200, sort: true},
                {
                    title: '操作',
                    minWidth: 150,
                    toolbar: '#currentTableBar',
                    fixed: "right",
                    align: "center",
                    hide: (!hasPermission('sys:role:edit') && !hasPermission('sys:role:delete'))
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
        let roleName = data.field.roleName;
        let description = data.field.description;
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
                roleName: roleName,
                description: description,
                startDate: startDate,
                endDate: endDate
            }
        }, 'data');

        return false;
    });

    /**
     * toolbar监听事件
     */
    table.on('toolbar(currentTableFilter)', function (obj) {
        if (obj.event === 'add') {  // 监听添加操作
            var index = layer.open({
                title: '添加角色',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '90%'],
                content: '/admin/page/role/role-add',
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
        } else if (obj.event === 'delete') {  // 监听删除操作
            var checkStatus = table.checkStatus('currentTableId')
                , data = checkStatus.data;
            var idList = [];
            $.each(data, function () {
                let id = $(this)[0].id;
                let userCount = $(this)[0].userCount;
                let roleName = $(this)[0].name;
                if (userCount >= 1) {
                    Swal.fire({
                        text: `角色: ${roleName}下存在关联用户，不能删除！`,
                        icon: 'warning',
                    })
                    return false;
                }
                idList.push(id);
            });
            if (idList.length <= 0) {
                return false;
            }
            Swal.fire({
                title: '确定删除选中的' + idList.length + '个角色吗？',
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
                            url: '/admin/role',
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
                title: '编辑角色',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '90%'],
                content: '/admin/role/' + data.id,
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
            return false;
        } else if (obj.event === 'delete') {
            let userCount = data.userCount;
            if (userCount >= 1) {
                Swal.fire({
                    text: `角色下存在关联用户，不能删除！`,
                    icon: 'warning',
                })
                return false;
            }
            Swal.fire({
                title: '确定删除选中的这个角色吗？',
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
                            url: '/admin/role/' + data.id,
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