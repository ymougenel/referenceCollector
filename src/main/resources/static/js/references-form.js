block='<div class="custom-control custom-checkbox">'+
    '<input type="checkbox" class="custom-control-input" id="input_added" value="42" name="labels"><input type="hidden" name="_labels" value="on">'+
    ' <label class="custom-control-label" for="42" id="label_added"></label>'+
    '</div>';

(function ($) {

    $.fn.add_label = function () {
        var label = {
            id: 0,
            name: $("#label_name_input").val()
        };
        $("#label_name_input").html("");
        $.ajax({
            url: "/label",
            method: "post",
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(label),
            success: function (label) {
                $("#insertHere").append( block );
                $("#label_added").html(label.name).attr("id", "label"+ label.id).attr("for",label.id);
                $("#input_added").val(label.id).attr("id", label.id).attr("checked",true);
            }
        });
    };

}(jQuery));