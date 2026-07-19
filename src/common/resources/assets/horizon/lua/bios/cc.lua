cc = {
    bios = {}
}
_G["cc"] = cc
function cc.bios:readInt(pos) return 0 end
function cc.bios:readShort(pos) return 0 end
function cc.bios:readByte(pos) return 0 end
function cc.bios:getBytes(pos, size) return {0} end
