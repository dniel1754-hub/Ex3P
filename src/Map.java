//package assignments.Ex3;
import java.util.LinkedList;
import java.util.Queue;

public class Map implements Map2D {
	private int[][] map;
	private boolean _cyclicFlag;

	public Map(int w, int h, int v) {
		this._cyclicFlag = true;
		this.init(w, h, v);
	}

	public Map(int size) {
		this(size, size, 0);
	}

	public Map(int[][] data) {
		this.init(data);
	}

	public void init(int w, int h, int v) {
		if (w != 0 && h != 0) {
			this.map = new int[w][h];

			for(int i = 0; i < this.map.length; ++i) {
				for(int j = 0; j < this.map[0].length; ++j) {
					this.map[i][j] = v;
				}
			}
		}

	}

	public void init(int[][] arr) {
		if (arr != null && arr.length >= 1 && arr[0].length >= 1) {
			this.map = new int[arr.length][arr[0].length];

			for(int i = 0; i < arr.length; ++i) {
				for(int j = 0; j < arr[0].length; ++j) {
					this.map[i][j] = arr[i][j];
				}
			}

		} else {
			throw new RuntimeException("Invalid array for init");
		}
	}

	public int[][] getMap() {
		int[][] ans = null;
		if (this.map == null) {
			return ans;
		} else {
			int w = this.getWidth();
			int h = this.getHeight();
			int[][] copy = new int[w][h];

			for(int i = 0; i < copy.length; ++i) {
				for(int j = 0; j < copy[0].length; ++j) {
					copy[i][j] = this.map[i][j];
				}
			}

			return copy;
		}
	}

	public int getWidth() {
		int ans = 0;
		return this.map == null ? ans : this.map.length;
	}

	public int getHeight() {
		int ans = 0;
		return this.map != null && this.map[0] != null ? this.map[0].length : ans;
	}

	public int getPixel(int x, int y) {
		int ans = -1;
		if (x < 0 || y < 0)
			return ans;
		return this.map[x][y];
	}


	public int getPixel(Pixel2D p) {
		int ans = -1;
		if (p.getX() < 0 || p.getY() < 0)
			return ans;
		return getPixel(p.getX(), p.getY());
	}

	public void setPixel(int x, int y, int v) {
		this.map[x][y] = v;
	}

	public void setPixel(Pixel2D p, int v) {
		this.setPixel(p.getX(), p.getY(), v);
	}

	public Queue<Pixel2D> getNeighbers(Pixel2D p, boolean cyclic) {
		Queue<Pixel2D> Neighbers = new LinkedList();
		int x = p.getX();
		int y = p.getY();
		int w = this.getWidth();
		int h = this.getHeight();
		int[] dx = new int[]{1, -1, 0, 0};
		int[] dy = new int[]{0, 0, 1, -1};

		for(int i = 0; i < 4; ++i) {
			int nextX = x + dx[i];
			int nextY = y + dy[i];
			if (cyclic) {
				nextX = (nextX + w) % w;
				nextY = (nextY + h) % h;
				Neighbers.add(new Index2D(nextX, nextY));
			} else if (nextX >= 0 && nextX < w && nextY >= 0 && nextY < h) {
				Neighbers.add(new Index2D(nextX, nextY));
			}
		}

		return Neighbers;
	}

	public int fill(Pixel2D xy, int new_v) {
		int ans = 0;
		int Pcolor = this.getPixel(xy);
		if (Pcolor == new_v) {
			return 0;
		} else {
			Queue<Pixel2D> list = new LinkedList();
			list.add(xy);
			this.setPixel(xy, new_v);
			int count = 1;

			while(!list.isEmpty()) {
				Pixel2D currunt = (Pixel2D)list.poll();

				for(Pixel2D neighber : this.getNeighbers(currunt, this._cyclicFlag)) {
					if (this.getPixel(neighber) == Pcolor) {
						this.setPixel(neighber, new_v);
						list.add(neighber);
						++count;
					}
				}
			}

			return count;
		}
	}

	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
		Pixel2D[] ans = null;
		Map2D copy = this.allDistance(p1, obsColor);
		if (copy.getPixel(p2) >= 0 && this.isInside(p2) && this.isInside(p1)) {
			int anslen = copy.getPixel(p2) + 1;
			ans = new Pixel2D[anslen];
			Pixel2D cur = p2;

			for(int i = anslen - 1; i >= 0; --i) {
				ans[i] = cur;
				if (i == 0) {
					break;
				}

				for(Pixel2D neighber : this.getNeighbers(cur, this._cyclicFlag)) {
					if (i - 1 == copy.getPixel(neighber)) {
						cur = neighber;
						break;
					}
				}
			}

			return ans;
		} else {
			return ans;
		}
	}

	public boolean isInside(Pixel2D p) {
		int x = p.getX();
		int y = p.getY();
		int h = this.getHeight();
		int w = this.getWidth();
		return x >= 0 && x < w && y >= 0 && y < h;
	}

	public boolean isCyclic() {
		return this._cyclicFlag;
	}

	public void setCyclic(boolean cy) {
		this._cyclicFlag = cy;
	}

	public Map2D allDistance(Pixel2D start, int obsColor) {
		Map2D ans = null;
		if (this.getPixel(start) != obsColor && this.isInside(start)) {
			ans = new Map(this.getWidth(), this.getHeight(), -1);
			ans.setPixel(start, 0);
			Queue<Pixel2D> List = new LinkedList();
			List.add(start);

			while(!List.isEmpty()) {
				Pixel2D cur = (Pixel2D)List.poll();
				int distocur = ans.getPixel(cur);

				for(Pixel2D neighber : this.getNeighbers(cur, this._cyclicFlag)) {
					if (this.getPixel(neighber) != obsColor && ans.getPixel(neighber) == -1) {
						ans.setPixel(neighber, distocur + 1);
						List.add(neighber);
					}
				}
			}

			return ans;
		} else {
			return ans;
		}
	}
}
