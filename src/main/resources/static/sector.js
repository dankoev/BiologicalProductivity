/* eslint no-undef:"warn" */
$(document).ready(() => {
  $('#getHeatMap').click(() => {
    $.get('/getHeatMap', null, (data, status) => {
      if (status === 'success' && data !== 'error') {
        console.log(data);
      }
    });
  });
});
