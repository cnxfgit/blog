layui.use(['table', 'treetable', 'form'], function () {
    var $ = layui.jquery;
    var table = layui.table;
    var treetable = layui.treetable;
    var form = layui.form;

    // 渲染表格
    layer.load(2);
    treetable.render({
        treeColIndex: 1,
        treeSpid: 0,
        treeIdName: 'id',
        treePidName: 'pid',
        elem: '#munu-table',
        url: '/admin/menu',
        page: false,
        cols: [
            [
                {type: 'numbers'},
                {field: 'title', minWidth: 200, title: '菜单标题'},
                {field: 'authority', title: '权限标识'},
                {field: 'href', title: '菜单链接'},
                {
                    field: 'sort', width: 80, align: 'center', title: '排序', sort: true, templet: function (data) {
                        let sort = data.sort;
                        return '<span class="layui-badge layui-bg-green">' + sort + '</span>'
                    }
                },
                {
                    field: 'icon', width: 80, align: 'center', title: '图标', templet: function (data) {
                        return '<span><i class="' + data.icon + '"></i></span>'
                    }
                },
                {
                    field: 'type', width: 80, align: 'center', templet: function (data) {
                        if (data.type === 1) {
                            return '<span class="layui-badge layui-bg-gray">目录</span>';
                        } else if (data.type === 2) {
                            return '<span class="layui-badge layui-bg-blue">菜单</span>';
                        } else if (data.type === 3) {
                            return '<span class="layui-badge layui-bg-green">按钮</span>';
                        }
                    }, title: '类型'
                },
                {
                    field: 'status', width: 80, align: 'center', title: '状态', templet: function (data) {
                        let status = data.status ? '显示' : '隐藏';
                        let color = (data.status ? 'green' : 'orange');
                        let bgColor = 'layui-bg-' + color;
                        return '<span class="layui-badge ' + bgColor + '">' + status + '</span>';
                    }
                },
                {
                    templet: '#auth-state',
                    width: 120,
                    align: 'center',
                    title: '操作',
                    hide: (!hasPermission('sys:menu:edit') && !hasPermission('sys:menu:delete'))
                }

            ]
        ],
        done: function () {
            layer.closeAll('loading');
            checkPermission();
        }
    });

    $('#btn-expand').click(function () {
        treetable.expandAll('#munu-table');
    });

    $('#btn-fold').click(function () {
        treetable.foldAll('#munu-table');
    });

    $('#btn-add').click(function () {
        var index = layer.open({
            title: '添加菜单',
            type: 2,
            shade: 0.2,
            maxmin: true,
            shadeClose: true,
            area: ['50%', '90%'],
            content: '/admin/page/menu/menu-add',
        });
        $(window).on("resize", function () {
            layer.full(index);
        });
    });

    //监听工具条
    table.on('tool(munu-table)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') {
            Swal.fire({
                title: '确定删除选中的这个菜单吗？',
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
                            url: '/admin/menu/' + data.id,
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
        } else if (layEvent === 'edit') {
            var index = layer.open({
                title: '编辑菜单',
                type: 2,
                shade: 0.2,
                maxmin: true,
                shadeClose: true,
                area: ['50%', '80%'],
                content: '/admin/menu/' + data.id,
            });
            $(window).on("resize", function () {
                layer.full(index);
            });
            return false;
        }
    });
});