import array


def trim_notes(line):
    if (len(line) == 0): return ""
    depth = 0
    stmt = []
    for c in line:
        if (c in "[({"): depth += 1
        elif (c in "])}"): depth -= 1
        elif (depth <= 0): stmt.append(c)

    if (len(stmt) == 0): return ""
    return array.array('B', list).tostring()


class Utterance:
    def __init__(self, idn=0, spkr="", utter="", raw_stmt="", stmt_trees=[]):
        self.id = idn
        self.spkr = spkr
        self.utter = utter
        self.raw_stmt = raw_stmt
        self.stmt_trees = stmt_trees

    def __gt__(self, other):
        return self.id > other.id

    def __cmp__(self, other):
        return self.id - other.id

    def __len__(self):
        return len(self.stmt_trees)

    def __iter__(self):
        return self.stmt_trees

    #   ================== FUNCTIONS ====================

    def add_stree(self, tree):
        self.stmt_trees.append(tree)

    #   ==================== GETTERS ====================

    def get_id(self):
        return self.id

    def get_spkr(self):
        return self.spkr

    def get_utter(self):
        return self.utter

    def get_stmt(self):
        return self.raw_stmt

    def get_strees(self):
        return self.stmt_trees

    #   ==================== SETTERS ====================

    def set_id(self, idn):
        self.id = idn

    def set_utter(self, utter):
        self.utter = utter

    def set_stmt(self, stmt):
        self.raw_stmt = stmt

    def set_strees(self, strees):
        self.stmt_trees = strees
