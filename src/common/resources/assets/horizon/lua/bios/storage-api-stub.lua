
-- use this on any peripheral.api
function api:readByte(self, position) end
function api:readShort(self, position) end
function api:readInt(self, position) end
function api:writeByte(self, position, value) end
function api:writeShort(self, position, value) end
function api:writeInt(self, position, value) end
function api:writeBytes(self, position, offset, length, ...) end
function api:getSize() end
