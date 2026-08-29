# Diode BlockEntity

This block assumes that your block model faces the direction `PosZ`

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
BlockEntityID: `horizon:diode-block-entity` <br>

BlockEntityArgs: <br>

```java
// 'inputPorts' takes in a list of directions to input power to
inPorts: []
// 'outPorts' takes in a list of directions to output power to
outPorts: []

// 'rotatePorts' when true, rotates the in and out ports
// to match up with the blockState param "direction"
rotatePorts: true or false

// 'invertInputs' when true swaps the on and off states
// when receiving and losing power
invertInputs: true or false

// 'invertOutputs' when true swaps the on and off states
// when removing or sending out power
invertOutputs: true or false

// 'delay' when used will add an x-tick delay to the
// when removing or sending out power
delay: 0 to infinity

// 'gateType' changes how the inputs are
// used to create the output. (examples ignore inversion)
// OR - outputs true if any input is true.
// AND - outputs true if all inputs are true.
// XOR - outputs true if an odd number of inputs are on, else false.
gateType: "OR" or "AND" or "XOR"
```

BlockEntity Signals (`usable with the "base:block_entity_signal" block action`)

```java
"turnOn" // switches the block-entity on
"turnOff" // switches the block-entity off
```

BlockState Triggers (`can be called by the block-entity when avalible`)

```java
"onTurnOn" // called when the block-entity is turned on
"onTurnOff" // called when the block-entity is turned off
```
