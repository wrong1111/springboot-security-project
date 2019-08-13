$(function () {
    menu = new ecp888({
        table: $("#dataGridTable"),
        form: $("#searchForm"),
        toolbar: "#salecontrolToolbar",
        dialog: $("#saveOrEdit"),
        columns: [[
            {field: 'id', title: 'id', width: 50},
            {field: 'parentId', title: '上级id', width: 50},
            {field: 'name', title: '菜单名称', width: 50},
            {field: 'urlpath', title: '跳转地址', width: 100},
            {field: 'grades', title: '层级序号', width: 25},
            {
                field: 'isvisable', title: '是否显示', width: 25,
                formatter: function (value, rec, index) {
                    if (value == 1) {
                        return "是";
                    } else return "否";
                }
            },
            {field: 'createTime', title: '创建时间', width: 60},
            {
                field: 'aa', title: '操作', align: 'center', width: 25,
                formatter: function (value, rec, index) {
                    var d = '<a href="javascript:void(0)"  onclick="menu.toEdit(' + index + ')">修改</a> ';
                    d += '<a href="javascript:void(0)"  onclick="menu.toCascadeDeleteMenu(' + rec.id + ')">删除</a> ';
                    return d;
                }
            }
        ]]
    });
    menu.toAddInner = function () {
        if ($.isEmptyObject($("#entity_pid").val())) {
            $.messager.alert("验证提示", "请在左边选择菜单。")
            return;
        }
        menu.toAdd()
    };
    menu.zTreeOnClick = function (event, treeId, treeNode) {
        var pid = treeNode.id;
        if (pid < 0) {
            pid = 0;
        }
        console.log("==>onclick,treeId=" + treeId + ",pid=" + pid);
        $("#eq_pid").val(pid);
        // set default value
        $("#entity_menuLevel").val(treeNode.level + 1);
        $("#entity_pid").val(pid);
        menu.search();
    };
    menu.refreshTree = function () {
        var nodes = menu.tree.getSelectedNodes();
        if (nodes.length > 0) {
            menu.tree.reAsyncChildNodes(nodes[0], "refresh");
        }
    };

    var setter = {
        data: {
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "parentId",
                rootPId: 0
            }
        },
        async: {
            enable: true,
            url: "/authmenu/findAllList",
            type: 'GET',
            autoParam: ["id"],
            otherParam: {
                "otherParam": "zTreeAsyncTest"
            }
        },
        callback: {
            onClick: menu.zTreeOnClick
        }
    };
    var tree = menu.tree = $.fn.zTree.init($('#tree'), setter);
    $("#eq_id").val("-999");
    menu.init();
});