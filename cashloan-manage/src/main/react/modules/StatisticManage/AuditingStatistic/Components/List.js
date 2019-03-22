import React from 'react'
import {Modal, Table} from 'antd';

var confirm = Modal.confirm;
const objectAssign = require('object-assign');
export default React.createClass({
  getInitialState() {
    return {
      selectedRowKeys: [], // 这里配置默认勾选列
      loading: false,
      data: [],
      pagination: {},
      canEdit: true,
      visible: false,
      visibleAdd:false,
    };
  },
  componentWillReceiveProps(nextProps, nextState) {
    this.clearSelectedList();
    this.fetch(nextProps.params);
  },
  hideModal() {
    this.setState({
      visible: false,
      visibleAdd:false
    });
    this.refreshList();
  },
  //新增跟编辑弹窗
  showModal(title, record, canEdit) {
    var record = record;
    this.setState({
      canEdit: canEdit,
      visible: true,
      title: title,
      record: record
    },()=>{
      this.refs.CustomerWin.setFieldsValue(record);
    });
  },
  //新增
  addModal(title, record, canEdit){
      this.setState({
        visibleAdd:true,
        title:title,  
      })

  },
  rowKey(record) {
    return record.id;
  },

  //分页
  handleTableChange(pagination, filters, sorter) {
    const pager = this.state.pagination;
    pager.current = pagination.current;
    this.setState({
      pagination: pager,
    });
    this.refreshList();
  },

  fetch(params = {}) {
    this.setState({
      loading: true
    });
    if (!params.pageSize) {
      var params = {};
      params = {
        pageSize: 10,
        current: 1,
      }
    }
    Utils.ajaxData({
      url: '/modules/manage/statistic/listAuditingStatistic.htm',
      data: params,

      callback: (result) => {
        const pagination = this.state.pagination;
        pagination.current = params.current;
        pagination.pageSize = params.pageSize;
        pagination.total = result.page.total;

        if (!pagination.current) {
          pagination.current = 1
        };
        this.setState({
          loading: false,
          data: result.data,
          pagination
        });
      }
    });
  },
  clearSelectedList() {
    this.setState({
      selectedRowKeys: [],
    });
  },
  refreshList() {
    var pagination = this.state.pagination;
    var params = objectAssign({}, this.props.params, {
      current: pagination.current,
      pageSize: pagination.pageSize,
    });
    this.fetch(params);
  },
  componentDidMount() {
    this.fetch();
  },

  onRowClick(record) {
    this.setState({
      selectedRowKeys: [record.id],
      selectedrecord: record
    });
  },
 
  render() {
    var columns = [{
        title: '统计时间',
        dataIndex: 'countTimeStr',
    }, {
      title: '申请笔数',
      dataIndex: 'borrowApplyCount',
    }, {
          title: '机审拒绝数',
          dataIndex: 'machineAuditNotPassCount',
    },{
      title: '机审通过数',
      dataIndex: 'machineAuditPassCount'
    }, {
      title: '人审笔数',
      dataIndex: "reviewCount",
    }, {
        title: '人审通过笔数',
        dataIndex: "reviewPassCount",
    }, {
        title: '人审拒绝笔数',
        dataIndex: "reviewNotPassCount",
    },{
        title: '放款笔数',
        dataIndex: "loadCount",
    }, {
        title: '机审通过率',
        dataIndex: "machineAuditPassRate",
        render(text,record){
          return record.machineAuditPassRate+"%"
        }
    },
    //     {
    //     title: '机审拒绝率',
    //     dataIndex: "machineAuditNotPassRate",
    //     render(text,record){
    //       return record.machineAuditNotPassRate +"%"
    //     }
    // },
        {
        title: '人审通过率',
        dataIndex: "reviewPassRate",
        render(text,record){
          return record.reviewPassRate +"%"
        }
    }
    //     {
    //     title: '人审拒绝率',
    //     dataIndex: "reviewNotPassRate",
    //     render(text,record){
    //       return record.reviewNotPassRate +"%"
    //     }
    // }
    ];
    var state = this.state;
    return (
      <div className="block-panel">

           <Table columns={columns} rowKey={this.rowKey}
             onRowClick={this.onRowClick}
             dataSource={this.state.data}
             pagination={this.state.pagination}
             loading={this.state.loading}
             onChange={this.handleTableChange}  />
         </div>
    );
  }
})