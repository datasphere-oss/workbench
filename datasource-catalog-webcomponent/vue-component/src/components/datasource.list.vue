<template>
  <div class="flex flex-col m-3">
     <div class="flex w-full justify-end">
      <button class="btn btn-primary" @click="editDataset()" v-if="hasDatasets">
        创建数据源
      </button>
      <a
        :href="datasourceWikiURL"
        target="_blank"
        class="btn btn-secondary ml-2"
        title="查看更多"
      >
        <FAIcon icon="question-circle"></FAIcon>
      </a>
    </div>
    <div class="flex w-full my-2" v-if="!hasDatasets">
      <CollapsableUi
        title="数据源"
        icon="database"
        :collapse-border="true"
      >
        <div class="p-5">
          <p class="py-4">
           没有数据源，通过加载您的第一个数据源开始使用 ML Workbench.
          </p>
          <button class="btn btn-primary" @click="editDataset()">
            创建数据源
          </button>
        </div>
      </CollapsableUi>
    </div> 
    <div class="flex my-2 w-full justify-end" v-if="hasDatasets">
      <div class="flex">
        <div class="flex inline-flex items-center">
          <select
            class="form-select mr-2 py-1"
            v-model="currentFilter"
            @change="setFilter()"
          >
            <option value disabled>通过过滤</option>
            <option
              v-for="(count, filter) in filters"
              :key="filter"
              :value="filter"
            >
              {{ filter | capitalize }} 载入数据源 ({{ count }})
            </option>
          </select>
        </div>
      </div>
       <div class="flex">
        <div class="flex inline-flex items-center">
          <select class="form-select mr-2 py-1" v-model="sortBy">
            <option value disabled>通过筛选</option>
            <option value="createdAt">创建日期</option>
            <option value="name">名称</option>
          </select>
          <input
            type="text"
            class="form-input py-1"
            placeholder="搜索数据源"
            v-model="searchTerm"
          />
        </div>
      </div> 
    </div> 
    <div class="flex flex-wrap" v-if="hasDatasets">
      <DatasetCard
        v-for="(dataset, index) in filteredOrderedAndSorted"
        :key="index"
        :dataset="dataset"
        @on-open-dataset="$emit('on-open-dataset', $event)"
      ></DatasetCard>
    </div>
    <div>
      <pagination-ui
        ref="pagination"
        v-if="!searchTerm"
        :total="filteredAndOrdered.length"
        :pageChanged="pageChanged"
        :itemsPerPage="itemsPerPage"
      />
    </div>
    <ModalUi
      :title="(activeDataset ? '编辑' : '创建') + ' 数据源'"
      size="md"
      v-if="isEdittingDataset"
      @onDismiss="isEdittingDataset = false"
    >
      <EditDatasetForm
        :data="activeDataset"
        @onSuccess="isEdittingDataset = false"
      />
    </ModalUi>
  </div>
</template>

<script>
import PaginationUi from "../vue-common/components/ui/pagination.ui";
import CollapsableUi from "../vue-common/components/ui/collapsable.ui";
import ModalUi from "../vue-common/components/ui/modal.ui";
import EditDatasetForm from "./forms/project/create-datasource.form";
import DatasetCard from "./datasource-card";

import { mapState } from "vuex";
import { filter } from "lodash-es";
import Vue2Filters from "vue2-filters";

export default {
  props: ["datasets", "sharedDatasets"],
  mixins: [Vue2Filters.mixin],
  components: {
    PaginationUi,
    CollapsableUi,
    ModalUi,
    EditDatasetForm,
    DatasetCard
  },
  computed: {
     hasDatasets() {
       return (this.datasets.length > 0 );
     },
    filteredAndOrdered() {
      let filtered = this.filterBy(this.datasets, this.searchTerm, "name");
      return this.orderBy(filtered, this.sortBy, -1);
    },
    filteredOrderedAndSorted() {
      if (this.searchTerm) {
        return this.filteredAndOrdered;
      }

      return this.limitBy(
        this.filteredAndOrdered,
        this.itemsPerPage,
        this.offset
      );
    },
    ...mapState("dataset", {
      loginAsOwner: state => state.loginAsOwner
    }),
    ...mapState("app", ["datasourceWikiURL", "globalError"]),
    filters() {
      return {
        all:  this.datasets.length,
      };
    },
    sortOptions() {
      if (this.sortBy === "") {
        return {};
      }

      return {
        enabled: true,
        initialSortBy: { field: this.sortBy, type: "desc" }
      };
    }
  },
  data() {
    return {
      currentFilter: "all",
      offset: 0,
      itemsPerPage: 8,
      searchTerm: "",
      sortBy: "createdAt",
      isEdittingDataset: false,
      activeDataset: null
    };
  },
  methods: {
    editDataset(dataset) {
      this.activeDataset = dataset;
      this.isEdittingDataset = true;
    },
    pageChanged(page) {
      this.offset = this.itemsPerPage * page.currentPage;
    },
    setFilter() {
      this.offset = 0;
      this.$refs.pagination.goToPage(1);
    }
  }
};
</script>
