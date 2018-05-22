package snippet;

public class Snippet {
	select gname from (pf_detail.groupinfo g join pf_detail.belong b on g.gid=b.bid)
	join pf_detail.memberinfo m on b.mid = m.mid where mac='ritakuo'
}

