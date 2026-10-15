function format(fmt){var a=Array.prototype.slice.call(arguments,1),i=0;return String(fmt).replace(/%s|%d|%j/g,function(m){var v=a[i++];if(m==='%j'){try{return JSON.stringify(v)}catch(e){return String(v)}}return String(v)})}
module.exports=format;module.exports.format=format
