# LaserPulser BlockEntity

This block assumes that your block model faces the diection `PosZ`

```python
'Directions' [
    NegX,
    PosX,
    NegY,
    PosY,
    NegZ,
    PosZ
]
```

<br><br>
BlockEntityID: `horizon:laser-pulser-block-entity` <br>

BlockEntityArgs: <br>

```java
// 'inputPorts' takes in a list of directions to input power to
inPorts: []
// 'outPorts' takes in a list of directions to output power to
outPorts: []

// 'rotatePorts' when true, rotates the in and out ports
// to match up with the blockState param "direction"
rotatePorts: true or false

// 'pulseCondition' decides whether the pulse should be
// sent when its turned on, off, or whenever there
// is a change in power
pulseCondition: "ON" or "OFF" or "BOTH"
```

BlockEntity Signals (`usable with the "base:block_entity_signal" block action`)
```java
"turnOn" // switches the block-entity on
"turnOff" // switches the block-entity off
"pulse" // sends a pulse to all outPorts
```

BlockState Triggers (`can be called by the block-entity when avalible`)
```java
"onTurnOn" // called when the block-entity is turned on
"onTurnOff" // called when the block-entity is turned off
```