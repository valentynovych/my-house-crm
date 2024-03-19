const currHref = window.location.pathname;
$('.menu-item a').each(function (i, item) {
    if (currHref === ($(item).attr('href'))) {
        $(item).parent().addClass('active')
        $(this).parents('[data-parrent]').addClass("active open");
    }
});