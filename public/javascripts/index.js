/**
 * Created by kolyan on 19.06.16.
 */
$('document').ready(function () {
    $('#shorten_btn').on('click', function () {
        $.ajax({
            url: '/shorten',
            type: 'POST',
            dataType: 'json',
            data: JSON.stringify({
                url: $('#shorten_url').val()
            }),

            success: function (data) {
                console.log(data);
            },
            error: function (data) {
                console.log(data);
            }
    })
        ;
    });
});