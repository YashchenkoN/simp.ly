$('document').ready(function () {
    $.ajax({
        url: '/api/getFromCookie',
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            console.log(data);
            var resultList = data.data;
            resultList.forEach(function(linkObject) {
                $('#anon_history').append(getHistoryFragment(linkObject))
            });
        },
        error: function (data) {
            console.log(data);
        }
    });

    $('#shorten_btn').on('click', function () {
        var url = $('#shorten_url').val();
        if (!/^https?:\/\//i.test(url)) {
            url = 'http://' + url;
        }
        $.ajax({
            url: '/api/shorten',
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                url: url
            }),
            success: function (data) {
                console.log(data);
                var linkObject = data.data;
                $('#anon_history').append(getHistoryFragment(linkObject))
            },
            error: function (data) {
                console.log(data);
                alert('Invalid url');
            }
        });
    });

    function getDomain() {
        return location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '') + '/';
    }

    function getHistoryFragment(linkObject) {
        var shortUrl = getDomain() + linkObject.shortUrl;
        var originalLink = linkObject.url;

        return '<li class="shortened_link">' +
                    '<div class="unauth-title">' +
                        '<a class="article-title" target="_blank" href="' + originalLink + '">' + originalLink + '</a>' +
                    '</div>' +
                    '<div class="unauth-title-url">' +
                        '<a class="article-title smaller" target="_blank" href="' + originalLink + '">' + originalLink + '</a>' +
                    '</div>' +
                    '<div class="unauth_capsule clearfix">' +
                        '<a class="short-url" target="_blank" href="' + shortUrl + '">' + shortUrl + '</a>' +
                        '<a class="copy button primary" target="_blank" data-clipboard-text="http://bit.ly/1w5Tyr2">Copy</a>' +
                        '<a class="info_page" target="_blank" href="/' + linkObject.shortUrl + '">' +
                            '<i class="default fa fa-bar-chart-o"></i> ' + linkObject.views +
                        '</a>' +
                    '</div>' +
                '</li>'
    }
});