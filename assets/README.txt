ASSET FOLDER STRUCTURE
======================

assets/
  guns/
    glock.jpg             ← Easy mode  (Glock)
    swiss_revolver.jpg    ← Normal mode (1872 Swiss Revolver)
    mossberg.jpg          ← Hard mode  (Mossberg 12 Gauge)

  players/
    player.jpg            ← Human player portrait (shown bottom-centre)
    dealer.jpg            ← AI dealer portrait    (shown top-centre)

  items/
    beer.jpg              ← Beer Can
    cigarette.jpg         ← Cigarette
    vodka.jpg             ← Vodka
    magnifying_glass.jpg  ← Magnifying Glass
    flip_phone.jpg        ← Flip Phone
    handcuffs.jpg         ← Handcuffs
    pill.jpg              ← Pill

IMAGE GUIDELINES
================
- Format : JPG or PNG (both work; rename to .jpg as listed above)
- Guns   : landscape, ~300x150 px recommended
- Players: portrait,  ~150x200 px recommended
- Items  : square,    ~64x64 px recommended

HOW THE GAME LOADS THEM
========================
ui/ImageLoader.java looks for each file at runtime.
If a file is missing, the slot shows a styled placeholder so
the game still runs without any assets.

Place your images in the correct subfolder and restart the game.
