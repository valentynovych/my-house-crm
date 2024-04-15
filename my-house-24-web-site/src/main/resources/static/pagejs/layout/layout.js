const currHref = window.location.href;
console.log(currHref);
$('.nav-link').each(function () {
    if (currHref.localeCompare($(this).prop('href')) === 0) {
        $(this).removeClass('text-white');
        $(this).addClass('text-secondary');
    }
});
