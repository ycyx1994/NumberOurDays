App({
  onLaunch() {
    try {
      const adapter = require('./domain/adapter')
      console.log('[NOD] domain ok', adapter.today())
    } catch (e) {
      console.error('[NOD] domain load failed', e)
    }
  },
  globalData: {
    n0: '#FAF7F2',
    n50: '#F3EEE6',
    n300: '#C9C0B2',
    n500: '#8A8175',
    n700: '#4A453E',
    n900: '#1C1916',
    accent: '#D4553A',
  },
})
