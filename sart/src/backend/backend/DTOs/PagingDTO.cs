namespace backend.DTOs
{
    public class PagingDTO
    {
        private int pageIndex;
        private int pageSize;

        public PagingDTO(int pageIndex, int pageSize)
        {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        public int PageSize { get => pageSize; set => pageSize = value; }
        public int PageIndex { get => pageIndex; set => pageIndex = value; }
    }
}
