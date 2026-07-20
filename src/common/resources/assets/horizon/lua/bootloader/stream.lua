local OutputStream = {}

function OutputStream.new(api)
    return setmetatable(
        {
            _position = 0,
            _api = api
        },OutputStream
    )
end

function OutputStream:writeByte()
    self._api.writeByte(self._position)
    self._position = self._position + 1
end

function OutputStream:writeShort()
    self._api.writeShort(self._position)
    self._position = self._position + 2
end

function OutputStream:writeInt()
    self._api.writeInt(self._position)
    self._position = self._position + 4
end

function OutputStream:writeBytes(offset, length, ...)
    self._api.writeInt(self._position, offset, length, {...})
    self._position = self._position + length
end

function OutputStream:setPosition(pos)
    self._position = pos
end

function OutputStream:getPosition()
    return self._position
end

local InputStream = {}

function InputStream.new(api)
    return setmetatable(
        {
            _position = 0,
            _api = api
        },InputStream
    )
end

function InputStream:readByte()
    local value = self._api.writeByte(self._position)
    self._position = self._position + 1
    return value
end

function InputStream:readShort()
    local value = self._api.readShort(self._position)
    self._position = self._position + 2
    return value
end

function InputStream:readInt()
    local value = self._api.readInt(self._position)
    self._position = self._position + 4
    return value
end

function InputStream:readBytes(size)
    local value = self._api.getBytes(self._position, size)
    self._position = self._position + size
    return value
end

function InputStream:setPosition(pos)
    self._position = pos
end

function InputStream:getPosition()
    return self._position
end

return {
    OutputStream = OutputStream,
    InputStream = InputStream
}