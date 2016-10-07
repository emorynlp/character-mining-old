import re
import bisect

RE_TAB = re.compile('\t')
BLANK_FIELD = "_"
ROOT_TAG = "@#r$%"


class StatementNode:
    def __init__(self, idn=0, form=ROOT_TAG, lemma=ROOT_TAG, pos=ROOT_TAG, feats=BLANK_FIELD, slabel=BLANK_FIELD,
                 dhead=None, deprel=None, ref_label=ROOT_TAG):
        self.id = idn
        self.word_form = form
        self.lemma = lemma
        self.part_of_speech_tag = pos
        self.feats = feats
        self.slabel = slabel
        self.dependency_head = dhead
        self.dependency_label = deprel
        self.dependent_list = []
        self.ref_label = ref_label

    def __gt__(self, other):
        return self.id > other.id

    def __str__(self):
        l = [str(self.id), self.word_form, self.lemma, self.part_of_speech_tag, str(self.feats)]

        if self.dependency_head:
            l.append(str(self.dependency_head.id))
            l.append(self.dependency_label)

        return "\t".join(l)

    #   ==================== GETTERS ====================

    def get_id(self):
        return self.id

    def get_wordform(self):
        return self.word_form

    def get_lemma(self):
        return self.lemma

    def get_postag(self):
        return self.part_of_speech_tag

    def get_semlabel(self):
        return self.slabel

    def get_dhead(self):
        return self.dependency_head

    def get_dlabel(self):
        return self.dependency_head

    def get_dlist(self):
        return self.dependent_list

    def get_subnodelist(self):
        sublist = []
        self.get_sublistaux(self, sublist)
        sublist.sort()
        return sublist

    def get_sublistaux(self, node, sublist):
        sublist.append(node)

        for child in node.getDependentList():
            self.get_sublistaux(child, sublist)

    def getReferentLabel(self):
        return self.ref_label

    #   ==================== SETTERS ====================

    def set_wordform(self, form):
        self.word_form = form

    def set_lemma(self, lemma):
        self.lemma = lemma

    def set_postag(self, tag):
        self.part_of_speech_tag = tag

    def set_dhead(self, node, label=None):
        if self.dependency_head is not None:
            self.dependency_head.dependent_list.remove(self)

        if node is not None:
            bisect.insort(node.dependent_list, self)

        self.dependency_head = node
        self.set_dlabel(label)

    def set_dlabel(self, dlabel):
        self.dependency_label = dlabel

    def set_reflabel(self, rlabel):
        self.ref_label = rlabel
