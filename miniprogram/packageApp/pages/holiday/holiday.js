const domain = require('../../domain/adapter')

Page({
  data: { rows: [] },
  onShow() {
    this.setData({ rows: domain.holidayList() })
  },
})
