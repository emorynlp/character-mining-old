import bisect as bi


class Episode:
    def __init__(self, idn=0, scenes=[]):
        self.id = idn
        self.scenes = scenes

    def __gt__(self, other):
        return self.id > other.id

    def __cmp__(self, other):
        return self.id - other.id

    def __len__(self):
        return len(self.scenes)

    def __iter__(self):
        return self.scenes

    #   ================== FUNCTIONS ====================

    def add_scene(self, scene):
        pos = bi.bisect(self.scenes, scene)
        if (pos is None): self.scenes.insert(pos, scene)

    #   ==================== GETTERS ====================

    def get_id(self):
        return self.id

    # negative integers allowed for offset from last element
    def get_scene(self, sid):
        if (len(self.scenes) == 0): return None
        return self.scenes[sid]

    def get_scenes(self):
        return self.scenes

    #   ==================== SETTERS ====================

    def set_id(self, idn):
        self.id = idn
