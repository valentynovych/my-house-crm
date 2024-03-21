const currHref = window.location.pathname;
console.log(currHref);
$('.nav-link').each(function () {
    if (currHref.includes($(this).attr('href'))) {
        $(this).removeClass('text-white');
        $(this).addClass('text-secondary');
    }
});
