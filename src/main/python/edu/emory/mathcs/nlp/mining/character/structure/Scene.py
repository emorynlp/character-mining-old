class Scene:
    def __init__(self, idn=0, utters=[]):
        self.id = idn
        self.utters = utters

    def __gt__(self, other):
        return self.id > other.id

    def __cmp__(self, other):
        return self.id - other.id

    def __len__(self):
        return len(self.utters)

    def __iter__(self):
        return self.utters

    #   ================== FUNCTIONS ====================

    def add_utter(self, utter):
        self.utters.append(utter)

    #   ==================== GETTERS ====================

    def get_id(self):
        return self.id

    def get_utter(self, uid):
        if (uid >= 0 and len(self.utters) > 0): return self.utters[uid]
        else: return None

    def get_utters(self):
        return self.utters

    #   ==================== SETTERS ====================

    def set_id(self, idn):
        self.id = idn
