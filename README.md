# FactoryGame
  This is a game im developing for funn and for practise.
  **It isnt ready for plying yet** as much of the buildings and functions and guis are missing

# Credits
  - Libary Gson: https://github.com/google/gson
  - Libary Zip4J: https://github.com/srikanth-lingala/zip4j
  - Libary javaluator: https://github.com/fathzer/javaluator, http://javaluator.sourceforge.net/en/home/

  - Anotations Libary by JetBrains

# Modding (Only Parcial Implementet)
  Modding suport is planned and parcial implementet

  *Ignore the documentation below as it is only for the already implementet features that only work on core game files*

# Modding Documentation (Only Parcial Implementet)

  1. Custom Guis
  2. Custom Colors
  3. Custom Fonts
  4. Static Gui Components
  5. Acive (Dynamic) Gui Components
  6. Interactive Gui Components
  7. Evaluatet Strings


# 1. Custom Guis
  - Guis follow a base scructure with components that define the look and behaivjor
  - Guis are written in a ´Guis.json´ and need to have a base structure of
    ```json
      "[Gui Name]": {
      "width": [number OR evaluatet string],
      "height": [number OR evaluatet string],
      "backgroundColor": [Color, see 2. Custom Colors]
      "numberFont": [Font, see 3. Custom Fonts]
      "staticGuiComponents": [
      {[Component]},
      {[Component]},
      ...
      ],
      "activeGuiComponents": [
      ...
      ],
      "interactiveGuiComponents": [
      ...
      ]
      }
    ```
  - static gui components are components mainly used for designing the gui the *can't* change after gui loading becourse
  they are chached into a immage on gui load
  - active or (dynamic) gui components are components that chanche during runtime for example animations
  - interactive gui components are components that enable a user interaction with the gui
  - a fully ready gui cann look like this:

<details><summary>Show</summary>
  - Current gui for buildings

  ```json
    "BuildingGuiInputOutputNonAnimated": {
    "width": 400,
    "height": 400,
    "backgroundColor":{
    "r": 135,
    "g": 135,
    "b": 135,
    "a": 120
    },
    "numberFont": {
    "fontName": "Century",
    "fontStyle": "Plain",
    "fontSize": 5
    },
    "staticGuiComponents":[
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 20,
    "startY": 104,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 84,
    "startY": 104,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 20,
    "startY": 168,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 84,
    "startY": 168,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 20,
    "startY": 232,
    "width": 64,
    "height": 64,
    "color": {
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 84,
    "startY": 232,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 252,
    "startY": 104,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 252,
    "startY": 168,
    "width": 64,
    "height": 64,
    "color": {
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 252,
    "startY": 232,
    "width": 64,
    "height": 64,
    "color": {
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 316,
    "startY": 104,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 316,
    "startY": 168,
    "width": 64,
    "height": 64,
    "color": {
    "alias": "BLACK"
    }
    },
    {
    "componentType": "GameCore.GuiElements.SquareNoFill",
    "strokeWeight": 5,
    "startX": 316,
    "startY": 232,
    "width": 64,
    "height": 64,
    "color":{
    "alias": "BLACK"
    }
    }
    ],
    "activeGuiComponents": [
    {
    "componentType": "GameCore.GuiElements.NonAnimatedGuiIcon",
    "startX": 168,
    "startY": 168,
    "width": 64,
    "height": 64
    },
    {
    "componentType": "GameCore.GuiElements.InputItemTexture",
    "slot": 0,
    "startX": 20,
    "startY": 104,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.InputItemTexture",
    "slot": 1,
    "startX": 84,
    "startY": 104,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.InputItemTexture",
    "slot": 2,
    "startX": 20,
    "startY": 168,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.InputItemTexture",
    "slot": 3,
    "startX": 84,
    "startY": 168,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.InputItemTexture",
    "slot": 4,
    "startX": 20,
    "startY": 232,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.InputItemTexture",
    "slot": 5,
    "startX": 84,
    "startY": 232,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.OutputItemTexture",
    "slot": 0,
    "startX": 252,
    "startY": 104,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.OutputItemTexture",
    "slot": 1,
    "startX": 252,
    "startY": 168,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.OutputItemTexture",
    "slot": 2,
    "startX": 252,
    "startY": 232,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.OutputItemTexture",
    "slot": 3,
    "startX": 316,
    "startY": 104,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.OutputItemTexture",
    "slot": 4,
    "startX": 316,
    "startY": 168,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    },
    {
    "componentType": "GameCore.GuiElements.OutputItemTexture",
    "slot": 5,
    "startX": 316,
    "startY": 232,
    "width": 64,
    "height": 64,
    "cacheFontMetrics": true,
    "textOffsetX": -2,
    "textOffsetY": -4
    }
    ],
    "interactiveGuiComponents": []
    }
  ```
</details>

# 2. Custom Colors
  - Colors can be generatet via RGB or Hex or an alias this scructure can be used where **[Color]** is named as type
  - they follow the pattern
    ```json
      { "r": [Number], "g": [Number], "b": [Number] }
    ```
      OR
    ```json
      { "alias": [alias] }
    ```
      OR
    ```json
      { "hex": [hex] }
    ```
  - alias decribes a constant from the ```java.awt.Color``` class like ```Color.BLACK``` so the alias is 
    ```"alias": "BLACK"```
  - see https://docs.oracle.com/javase/7/docs/api/java/awt/Color.html -> **Field Summary** for avaidable alias

# 3. Custom Fonts
  - Fonts also follow a base scructure, this sctructure can be used where **[Font]** is the type
    ```json
      {
      "fontName": "[Font Name]",
      "fontStyle": "Plain OR Bold OR Italic",
      "fontSize": [number OR evaluatet string]
      }
    ```
  - Font Names can be:

<details><summary>Show</summary>
  -Reference: https://alvinalexander.com/blog/post/jfc-swing/swing-faq-list-fonts-current-platform/

  ```cs
    #GungSeo
    #HeadLineA
    #PCMyungjo
    #PilGi
    Abadi MT Condensed Extra Bold
    Abadi MT Condensed Light
    Academy Engraved LET
    Al Bayan
    American Typewriter
    Andale Mono
    Apple Casual
    Apple Chancery
    Apple LiGothic
    Apple LiSung
    Apple Symbols
    AppleGothic
    AppleMyungjo
    Arial
    Arial Black
    Arial Hebrew
    Arial Narrow
    Arial Rounded MT Bold
    Ayuthaya
    Baghdad
    Bank Gothic
    Baskerville
    Baskerville Old Face
    Batang
    Bauhaus 93
    Bell MT
    Bernard MT Condensed
    BiauKai
    Big Caslon
    Bitstream Vera Sans
    Bitstream Vera Sans Mono
    Bitstream Vera Serif
    Blackmoor LET
    BlairMdITC TT
    Bodoni Ornaments ITC TT
    Bodoni SvtyTwo ITC TT
    Bodoni SvtyTwo OS ITC TT
    Bodoni SvtyTwo SC ITC TT
    Book Antiqua
    Bookman Old Style
    Bordeaux Roman Bold LET
    Bradley Hand ITC TT
    Braggadocio
    Britannic Bold
    Brush Script MT
    Calisto MT
    Century
    Century Gothic
    Century Schoolbook
    Chalkboard
    Charcoal CY
    Cochin
    Colonna MT
    Comic Sans MS
    Cooper Black
    Copperplate
    Copperplate Gothic Bold
    Copperplate Gothic Light
    Corsiva Hebrew
    Courier
    Courier New
    Cracked
    Curlz MT
    DecoType Naskh
    Desdemona
    Devanagari MT
    Dialog
    DialogInput
    Didot
    Edwardian Script ITC
    Engravers MT
    Euphemia UCAS
    Eurostile
    Footlight MT Light
    Futura
    Garamond
    GB18030 Bitmap
    Geeza Pro
    Geneva
    Geneva CY
    Georgia
    Gill Sans
    Gill Sans Ultra Bold
    Gloucester MT Extra Condensed
    Goudy Old Style
    Gujarati MT
    Gulim
    Gurmukhi MT
    Haettenschweiler
    Handwriting - Dakota
    Harrington
    Hei
    Helvetica
    Helvetica CY
    Helvetica Neue
    Herculanum
    Hiragino Kaku Gothic Pro
    Hiragino Kaku Gothic Std
    Hiragino Maru Gothic Pro
    Hiragino Mincho Pro
    Hoefler Text
    Impact
    Imprint MT Shadow
    InaiMathi
    Jazz LET
    Kai
    Kino MT
    Krungthep
    KufiStandardGK
    LiHei Pro
    LiSong Pro
    Lucida Blackletter
    Lucida Bright
    Lucida Calligraphy
    Lucida Fax
    Lucida Grande
    Lucida Handwriting
    Lucida Sans
    Lucida Sans Typewriter
    Marker Felt
    Matura MT Script Capitals
    Mistral
    Modern No. 20
    Mona Lisa Solid ITC TT
    Monaco
    Monospaced
    Monotype Corsiva
    Monotype Sorts
    MS Gothic
    MS Mincho
    MS PGothic
    MS PMincho
    Mshtakan
    MT Extra
    Nadeem
    New Peninim MT
    News Gothic MT
    Onyx
    OpenSymbol
    Optima
    Osaka
    Palatino
    Papyrus
    Party LET
    Perpetua Titling MT
    Plantagenet Cherokee
    Playbill
    PMingLiU
    PortagoITC TT
    Princetown LET
    Raanana
    Rockwell
    Rockwell Extra Bold
    SansSerif
    Santa Fe LET
    Sathu
    Savoye LET
    SchoolHouse Cursive B
    SchoolHouse Printed A
    Serif
    Silom
    SimSun
    Skia
    Snell Roundhand
    Stencil
    STFangsong
    STHeiti
    STKaiti
    Stone Sans ITC TT
    Stone Sans Sem ITC TT
    STSong
    Symbol
    Synchro LET
    Tahoma
    Thonburi
    Times
    Times New Roman
    Trebuchet MS
    Type Embellishments One LET
    Verdana
    Webdings
    Wide Latin
    Wingdings
    Wingdings 2
    Wingdings 3
    Zapf Dingbats
    Zapfino
  ```

</details>

# 4. Static Gui Components
  - Static gui components are for drawing and designing the gui ant cant chanche during runtime as statet in 1. Custom Guis
  - Components all hava a ```"componentType"``` that defines the type (Path to a class in the game files)
  ### Square No Fill

<details><summary>Show</summary>
  - Syntax:
  
  ```json
    {
      "componentType": "GameCore.GuiElements.SquareNoFill",
      "strokeWeight": [number OR evaluatet string],
      "startX": [number OR evaluatet string],
      "startY": [number OR evaluatet string],
      "width": [number OR evaluatet string],
      "height": [number OR evaluatet string],
      "color": [Color, see 2. Custom Colors]
    }
  ```

  - will draw a hollow square, parameters should be self explanetory
</details>

  ### Square Fill

<details><summary>Show</summary>
  - Syntax:
  
  ```json
    {
      "componentType": "GameCore.GuiElements.SquareFill",
      "strokeWeight": [number OR evaluatet string],
      "startX": [number OR evaluatet string],
      "startY": [number OR evaluatet string],
      "width": [number OR evaluatet string],
      "height": [number OR evaluatet string],
      "color": [Color, see 2. Custom Colors]
    }
  ```

  - will draw a filled square, parameters should be self explanetory
</details>

# 5. Acive (Dynamic) Gui Components

# 6. Interactive Gui Components

# 7. Evaluatet Strings
  - Evaluatet strings can be used on propertys where type is ```[number OR evaluatet string]```
  - Evaluatet strings always begin with a ```$```
  - Evaluatet strings can only use **Placeholders** and **Numeric Expressions**
  - A example could be: ```"${windowWidth} / 2"``` or ```"$4 * 4"```
  - The result gets calculatet with Doubble point precision but gets castet to Intager

### Placeholders
  - ```{windowWidth}``` represents current windowWidth
  - ```{WindowHeight}``` represents current windowHeight

### Numeric Expressions
  - ```+ - * /```
  - ```()```
  - ```sin()``` and other common math functions
  - binary operators
  - **see souce libary: http://javaluator.sourceforge.net/en/home/**
