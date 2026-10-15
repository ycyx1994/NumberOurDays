// Minimal format() shim for WeChat (kotlinx-datetime only needs light formatting).
function format(fmt) {
  var args = Array.prototype.slice.call(arguments, 1)
  var i = 0
  return String(fmt).replace(/%s|%d|%j/g, function (m) {
    var v = args[i++]
    if (m === '%j') {
      try { return JSON.stringify(v) } catch (e) { return String(v) }
    }
    return String(v)
  })
}
module.exports = format
module.exports.format = format
