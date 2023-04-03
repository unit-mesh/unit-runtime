import TreeView from "@mui/lab/TreeView";
import TreeItem, { TreeItemProps } from "@mui/lab/TreeItem";

export type FileTreeItem = {
  path: string;
  name: string;
  type: "file" | "folder";
  children?: FileTreeItem[];
};

export type Props = {
  items: FileTreeItem[];
};

export default function FileTree({ items = [] }: Props) {
  return (
    <div>
      <TreeView>{renderItems(items)}</TreeView>
    </div>
  );
}

function renderItems(items: FileTreeItem[] | undefined) {
  if (!items) return null;

  return items.map((item) => {
    return (
      <TreeItem
        key={item.path}
        nodeId={item.path}
        label={item.name}
        children={renderItems(item.children)}
      />
    );
  });
}
