const dataLang = document.querySelector('html').getAttribute('lang');
// set localization to flatpickr
if (dataLang && dataLang === 'uk') {
    flatpickr.localize(flatpickr.l10ns.uk);
}

// config to toastr
toastr.options = {
    maxOpened: 1,
    newestOnTop: true,
    progressBar: true,
    preventDuplicates: true,
};