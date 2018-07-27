#!/usr/bin/python3

import pngs_from_svg as pfs


class Icon:
    def __init__(self, source, dest):
        self.source = source
        self.dest = dest


class Config:
    def __init__(self, color, opacity, suffix):
        self.color = color
        self.opacity = opacity
        self.suffix = suffix

ICONS = [
    Icon("ic_swap_vert_48px", "m_swap_direction"),
    Icon("ic_info_48px", "m_info"),
    ]

CONFIGS = [
    Config("#fff", 1.0, ""),
    ]

res = "../../app/src/main/res"
isize = 24

for icon in ICONS:
    svg = icon.source + ".svg"
    dest = icon.dest
    for config in CONFIGS:
        pfs.create_images(svg, res, dest, config.suffix, isize,
                           config.color, config.opacity)
