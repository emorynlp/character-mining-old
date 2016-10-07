import bisect as bi


class Season:
    def __init__(self, idn=0, episodes=[]):
        self.id = idn
        self.eps = episodes

    def __gt__(self, other):
        return self.id > other.id

    def __cmp__(self, other):
        return self.id - other.id

    def __len__(self):
        return len(self.eps)

    def __iter__(self):
        return self.eps

    #   ================== FUNCTIONS ====================

    def add_ep(self, ep):
        pos = bi.bisect(self.eps, ep)
        if (pos is None): self.eps.insert(pos, ep)

    #   ==================== GETTERS ====================

    def get_id(self):
        return self.id

    def get_ep(self, eid):
        if (eid < 0 or len(self.eps) <= eid): return None
        return self.eps[eid]

    def get_eps(self):
        return self.eps

    #   ==================== SETTERS ====================

    def set_id(self, idn):
        self.id = idn
