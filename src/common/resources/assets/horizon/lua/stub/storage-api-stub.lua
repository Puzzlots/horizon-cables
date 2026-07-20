local apistub = {
    storage = {},
    screen = {
        palette = {}
    },
    image = {}
}
_G["stub"] = apistub

-- use this on any peripheral.api
function apistub.storage.readByte(position) return 0 end
function apistub.storage.readShort(position) return 0 end
function apistub.storage.readInt(position) return 0 end
function apistub.storage.getBytes(position, size) return {} end

function apistub.storage.writeByte(position, value) end
function apistub.storage.writeShort(position, value) end
function apistub.storage.writeInt(position, value) end
function apistub.storage.writeBytes(position, offset, length, ...) end
function apistub.storage.getSize() end


function apistub.screen.palette.setColor(idx, color) end
function apistub.screen.palette.setColors(src, offs, length, destIdx) end
function apistub.screen.palette.getColor(idx) return 0 end
function apistub.screen.palette.getSize() return 0 end

function apistub.screen.getWidth() return 0 end
function apistub.screen.getHeight() return 0 end
function apistub.screen.swap() end
function apistub.screen.getPalette() end
function apistub.screen.getPixel(x, y) return 0 end
function apistub.screen.setPixel(x, y, idx) end
function apistub.screen.fill(idx) end

function apistub.screen.drawRect(x, y, width, height, idx, doFill) end
function apistub.screen.drawCircle(x, y, width, height, idx, doFill) end

function apistub.image.getWidth() return 0 end
function apistub.image.getHeight() return 0 end
function apistub.image.getPixel(x, y) return 0 end
function apistub.image.setPixel(x, y, color) end
