/* eslint no-undef:"warn" */
$(document).ready(() => {
  $('#getHeatMap').click(() => {
    $.ajax({
      url: '/getHeatMap',
      method: 'GET',
      xhrFields: {
        responseType: 'blob',
      },
      success(data) {
        const imageUrl = URL.createObjectURL(data);
        const img = $('<img>').attr({ src: imageUrl, id: 'sector' });
        $('#imageContainer').append(img);
      },
    });
  });
});
