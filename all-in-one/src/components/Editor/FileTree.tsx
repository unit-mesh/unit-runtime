import { IconButton } from "@mui/material";
import TreeView from "@mui/lab/TreeView";
import TreeItem, { TreeItemProps } from "@mui/lab/TreeItem";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRefresh, faDownload } from "@fortawesome/free-solid-svg-icons";

export type FileTreeItem = {
  path: string;
  name: string;
  type: "file" | "folder";
  children?: FileTreeItem[];
};

export type Props = {
  items: FileTreeItem[];
  refresh: () => void;
  downloadDeps: () => void;
  onSelected: (item: FileTreeItem) => void;
};

export default function FileTree({
  items = [],
  refresh,
  onSelected,
  downloadDeps,
}: Props) {
  return (
    <div>
      <div>
        {/* top */}
        <IconButton
          onClick={() => {
            refresh?.();
          }}>
          <FontAwesomeIcon icon={faRefresh} />
        </IconButton>
        <IconButton
          onClick={() => {
            downloadDeps?.();
          }}>
          <FontAwesomeIcon icon={faDownload} />
        </IconButton>
      </div>
      <TreeView
        multiSelect={false}
        onNodeSelect={(_, id: string) => {
          const item = findItem(items, id);
          if (item) {
            onSelected(item);
          }
        }}>
        {<Items items={items} />}
      </TreeView>
    </div>
  );
}

function findItem(items: FileTreeItem[], path: string): FileTreeItem | null {
  for (const item of items) {
    if (item.path === path) return item;
    if (item.children) {
      const r = findItem(item.children, path);
      if (r) return r;
    }
  }

  return null;
}

function Items({ items = [] }: { items: FileTreeItem[] | undefined }) {
  if (!items) return <></>;

  return (
    <>
      {items.map((item) => {
        return (
          <TreeItem
            key={item.path}
            nodeId={item.path}
            label={item.name}
            children={<Items items={item.children} />}
          />
        );
      })}
    </>
  );
}
