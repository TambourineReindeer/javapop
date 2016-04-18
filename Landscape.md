# Introduction #
The original populous used an isometric display. The landscape was a square heightmap, rendered with hand-drawn bitmaps. The graphic used for a given tile was determined by the absolute height of the tile (Sea level tiles are obviously different from grassed tiles, and the sea-level to grass transition was a sandy beach) and the relative heights of the four corners of the tile. The height of each tile also cannot change more than one unit between adjacent tiles - this means there are no cliffs, and repeatedly raising a corner results in a large, even sided pyramid.

I'm replicating this in 3d (much easier and more flexible these days). To render each tile, we break it up into triangles. There is more than one way to decompose a quadrilateral however!
Consider a tile that has three corners at height 1 and one corner at height 2. There are two ways to draw two triangles that fit those corners, and only one is the 'populous' way! If we create a new vertex in the middle of the tile, making four triangles, we can ensure the correct rendering, no matter the pattern of heights. This new vertex should be half a unit above the bottom of the tile.

Images coming.