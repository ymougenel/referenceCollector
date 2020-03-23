block = '<div class="custom-control custom-checkbox">' +
    '<input type="checkbox" class="custom-control-input" id="input_added" value="42" name="labels"><input type="hidden" name="_labels" value="on">' +
    ' <label class="custom-control-label" for="42" id="label_added"></label>' +
    '</div>';

// Create a new label based on the label_name_input
(function ($) {

    $.fn.add_label = function () {
        var label = {
            id: 0,
            name: $("#label_name_input").val()
        };
        $("#label_name_input").html("");
        var token = $("input[name='_csrf']").val();
        var header = "X-CSRF-TOKEN";
        $.ajax({
            url: "/label",
            method: "POST",
            crossDomain: true,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(label),
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
                xhr.setRequestHeader(header, token);
            },
            success: function (label) {
                $("#insertHere").append(block);
                $("#label_added").html(label.name).attr("id", "label" + label.id).attr("for", label.id);
                $("#input_added").val(label.id).attr("id", label.id).attr("checked", true);
            }
        });
    };

}(jQuery));

// Autocomplete the reference name from the url input
$('#url_input').bind('input', function () {
    var url = $(this).val();
    var API_URL = "http://textance.herokuapp.com/title/";
    $.ajax({
        url: API_URL + url,
        complete: function (data) {
            var nameInput = $("#name_input");
            if (nameInput.val() === "") {
                nameInput.val(data.responseText)
            }
        }
    });
});
