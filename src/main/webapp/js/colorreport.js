$(function() {
    // Attach the dynatree widget to an existing <div id="tree"> element
    // and pass the tree options as an argument to the dynatree() function:
    $("#tree").dynatree({
        title: "System",
        selectMode:1,
        autoCollapse:true,

        initAjax: {
            url: "servlets/treedata",
            data: { type:'geo' }
        },

        onLazyRead: function(dtnode) {
            dtnode.appendAjax({
                url:"servlets/treedata",
                data: {
                    id:dtnode.data.key,
                    type: 'geo'
                }
            })
        },

        cache: false
    });
    $("#runbutton").button().bind("click", function() {
        var node = $('#tree').dynatree('getActiveNode')
        var startdate = $("#ComboBox option:selected").value()
        //var startdate = $('#datepicker').datepicker("getDate")

        if (!node)
            return

        var obj = {"location":node.data.key, "startdate":startdate};
        $.get("servlets/results", obj, function(data) {
            $("#results").html(data)
        })
    })
});












